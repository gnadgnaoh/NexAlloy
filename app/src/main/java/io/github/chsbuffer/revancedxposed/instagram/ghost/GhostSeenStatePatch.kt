package io.github.chsbuffer.revancedxposed.instagram.ghost

import app.revanced.extension.shared.Logger
import io.github.chsbuffer.revancedxposed.hookMethod
import io.github.chsbuffer.revancedxposed.patch

val GhostSeenState = patch(
    name = "Ghost seen state",
    description = "Blocks DM read receipts from being sent.",
) {
    ::seenStateFingerprint.hookMethod {
        before {
            Logger.printDebug { "Ghost: DM seen state blocked" }
            it.result = null
        }
    }
}
