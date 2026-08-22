package io.github.nexalloy

import android.os.Handler
import android.os.Looper
import app.morphe.extension.shared.Logger
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Deferred patch scheduler for apps whose bytecode is not in `base.apk`.
 *
 * Facebook keeps ~20 secondary dex files Superpack-compressed under `assets/` and loads
 * them through `com.facebook.common.dextricks`. A [DexSource.CLASS_LOADER] DexKit bridge
 * therefore only sees whatever dex happens to be installed at the moment it is opened,
 * and NexAlloy applies patches once at `Application.onCreate` — early enough that the
 * feed dex may still be missing. When that happens every fingerprint quietly resolves to
 * nothing and the patch "succeeds" while hooking absolutely nothing.
 *
 * This gate fixes that without changing behaviour for any other app:
 *
 *  1. A **readiness probe** asks the bridge for a class carrying a stable, non-obfuscated
 *     marker string from the feed code. Until that resolves, the pass is abandoned and
 *     nothing is written to the fingerprint cache.
 *  2. `MultiDexClassLoaderJava.configure` is hooked so a pass runs the moment the loader
 *     finishes installing dex files, rather than waiting for the next timer.
 *  3. Timed retries cover long-tail dex that arrive after start-up.
 *
 * The last scheduled pass runs unconditionally so partial results are still applied and
 * reported even if the probe never succeeded.
 */
internal class KatanaDexGate(private val executor: PatchExecutor) {

    private companion object {
        /**
         * Real, non-obfuscated class names that live in the feed dex — the same ones the
         * ad fingerprints match parameter types against. If the loader can produce these,
         * the dex holding the feed code is installed and a DexKit pass is worth running.
         * Nothing here is version pinned: these names have been stable for years.
         */
        val PROBE_CLASSES = listOf(
            "com.facebook.graphql.model.GraphQLFeedUnitEdge",
            "com.facebook.auth.usersession.FbUserSession",
        )

        /** Delays, in ms, measured from Application.onCreate. */
        val RETRY_DELAYS_MS = longArrayOf(0, 400, 1_500, 4_000, 10_000, 25_000)

        const val DEX_LOADER_CLASS = "com.facebook.common.dextricks.MultiDexClassLoaderJava"
    }

    private val handler = Handler(Looper.getMainLooper())
    private val running = AtomicBoolean(false)
    private val finished = AtomicBoolean(false)
    private val loaderHooks = mutableListOf<XC_MethodHook.Unhook>()

    fun start() {
        hookDexLoader()
        RETRY_DELAYS_MS.forEachIndexed { index, delay ->
            val isLast = index == RETRY_DELAYS_MS.lastIndex
            handler.postDelayed({ attempt(finalAttempt = isLast) }, delay)
        }
    }

    /**
     * Best effort: runs a pass as soon as the dex loader finishes a configure() call.
     * If the class or method is absent on this build, the timers alone still cover it.
     */
    private fun hookDexLoader() = runCatching {
        val loaderClass = XposedHelpers.findClassIfExists(DEX_LOADER_CLASS, executor.classLoader)
            ?: return@runCatching
        loaderClass.declaredMethods
            .filter { it.name == "configure" && it.parameterCount == 1 }
            .forEach { method ->
                method.isAccessible = true
                loaderHooks += XposedBridge.hookMethod(method, object : XC_MethodHook() {
                    override fun afterHookedMethod(param: MethodHookParam) {
                        if (finished.get()) return
                        handler.post { attempt(finalAttempt = false) }
                    }
                })
            }
    }.onFailure { XposedBridge.log(it) }

    private fun attempt(finalAttempt: Boolean) {
        if (finished.get()) return
        // Passes are serialised: a timer must not overlap a configure()-triggered run.
        if (!running.compareAndSet(false, true)) return
        try {
            val done = executor.runDeferredAttempt(finalAttempt) { isDexReady() }
            if (done || finalAttempt) {
                finished.set(true)
                // Cancel the remaining timers and detach the loader hook: once settled
                // this gate must cost the process exactly nothing.
                handler.removeCallbacksAndMessages(null)
                loaderHooks.forEach { runCatching { it.unhook() } }
                loaderHooks.clear()
                Logger.printDebug {
                    "KatanaDexGate: settled (complete=$done, outstanding=${executor.outstandingPatchCount})"
                }
            }
        } catch (err: Throwable) {
            XposedBridge.log(err)
        } finally {
            running.set(false)
        }
    }

    private fun isDexReady(): Boolean = PROBE_CLASSES.all { name ->
        XposedHelpers.findClassIfExists(name, executor.classLoader) != null
    }
}
