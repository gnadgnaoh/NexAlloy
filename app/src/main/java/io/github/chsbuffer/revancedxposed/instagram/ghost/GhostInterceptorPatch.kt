package io.github.chsbuffer.revancedxposed.instagram.ghost

import app.revanced.extension.shared.Logger
import de.robv.android.xposed.XposedHelpers
import io.github.chsbuffer.revancedxposed.instagram.network.networkInterceptorFingerprint
import io.github.chsbuffer.revancedxposed.hookMethod
import io.github.chsbuffer.revancedxposed.patch
import java.net.URI

val GhostInterceptor = patch(
    name = "Ghost interceptor",
    description = "Blocks ghost mode network requests via TigonServiceLayer (screenshot, view once, story seen).",
) {
    ::networkInterceptorFingerprint.member.hookMethod {
        before {
            val obj = it.args[0] ?: return@before
            val uriFieldName = obj.javaClass.declaredFields
                .firstOrNull { f -> f.type == URI::class.java }?.name ?: return@before
            val uri = XposedHelpers.getObjectField(obj, uriFieldName) as? URI ?: return@before
            val path = uri.path ?: return@before

            val block =
                // Screenshot
                path.endsWith("/screenshot/") ||
                path.endsWith("/ephemeral_screenshot/") ||
                // View once
                path.endsWith("/item_replayed/") ||
                (path.contains("/direct") && path.endsWith("/item_seen/")) ||
                // Story seen
                path.contains("/api/v2/media/seen/")

            if (block) {
                Logger.printDebug { "Ghost interceptor blocked: $path" }
                XposedHelpers.setObjectField(obj, uriFieldName,
                    URI("https", "0.0.0.0", "/0", null))
            }
        }
    }
}
