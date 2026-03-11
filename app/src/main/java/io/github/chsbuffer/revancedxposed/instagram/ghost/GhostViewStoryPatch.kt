package io.github.chsbuffer.revancedxposed.instagram.ghost

import app.revanced.extension.shared.Logger
import io.github.chsbuffer.revancedxposed.hookMethod
import io.github.chsbuffer.revancedxposed.patch

val GhostViewStory = patch(
    name = "Ghost view story",
    description = "Prevents story seen notifications from being sent to the uploader.",
) {
    ::storySeenFingerprint.hookMethod {
        before {
            Logger.printDebug { "Ghost: story seen blocked" }
            it.result = null
        }
    }
}
