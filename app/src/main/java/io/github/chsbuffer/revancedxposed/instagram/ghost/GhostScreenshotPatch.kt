package io.github.chsbuffer.revancedxposed.instagram.ghost

import app.revanced.extension.shared.Logger
import io.github.chsbuffer.revancedxposed.hookMethod
import io.github.chsbuffer.revancedxposed.patch

val GhostScreenshot = patch(
    name = "Ghost screenshot",
    description = "Blocks screenshot notification from being sent to the other party.",
) {
    ::screenshotFingerprint.hookMethod {
        before {
            Logger.printDebug { "Ghost: screenshot notification blocked" }
            it.result = null
        }
    }
}
