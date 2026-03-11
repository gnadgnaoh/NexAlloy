package io.github.chsbuffer.revancedxposed.instagram.ghost

import app.revanced.extension.shared.Logger
import io.github.chsbuffer.revancedxposed.hookMethod
import io.github.chsbuffer.revancedxposed.patch

val GhostTypingStatus = patch(
    name = "Ghost typing status",
    description = "Blocks typing indicator from being sent to the other party.",
) {
    ::typingStatusFingerprint.hookMethod {
        before {
            Logger.printDebug { "Ghost: typing status blocked" }
            it.result = null
        }
    }
}
