package io.github.chsbuffer.revancedxposed.instagram.ghost

import app.revanced.extension.shared.Logger
import io.github.chsbuffer.revancedxposed.hookMethod
import io.github.chsbuffer.revancedxposed.patch

val GhostViewOnce = patch(
    name = "Ghost view once",
    description = "Prevents view-once seen notifications from being sent.",
) {
    ::viewOnceFingerprint.hookMethod {
        before {
            val obj = it.args[2] ?: return@before
            for (method in obj.javaClass.declaredMethods) {
                if (method.parameterTypes.isNotEmpty() || method.returnType != String::class.java) continue
                try {
                    method.isAccessible = true
                    val value = method.invoke(obj) as? String ?: continue
                    if (value.contains("visual_item_seen") || value.contains("send_visual_item_seen_marker")) {
                        Logger.printDebug { "Ghost: view-once seen marker suppressed" }
                        it.result = null
                    }
                } catch (_: Throwable) {}
            }
        }
    }
}
