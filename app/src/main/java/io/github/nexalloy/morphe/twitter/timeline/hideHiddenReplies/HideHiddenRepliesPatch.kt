package io.github.nexalloy.morphe.twitter.timeline.hideHiddenReplies

import app.morphe.extension.shared.Logger
import io.github.nexalloy.setBooleanField
import io.github.nexalloy.patch

val HideHiddenReplies = patch(
    name = "Hide hidden replies",
    description = "Hides the \"hidden replies\" indicator/entry point on tweets.",
) {
    HideHiddenRepliesFingerprint.hookMethod {
        before { param ->
            val instance = param.thisObject ?: return@before
            runCatching {
                instance.setBooleanField("n", false)
            }.onFailure { e ->
                Logger.printException({ "[Twitter] HideHiddenReplies: failed to set field" }, e)
            }
        }
    }
}

