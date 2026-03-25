package io.github.nexalloy.morphe.reddit.ad.comments

import app.morphe.extension.shared.Logger
import io.github.nexalloy.patch

val HideCommentAds = patch(
    description = "Removes ads in the comments.",) {
    ::hideCommentAdsFingerprint.hookMethod {
        before {
            Logger.printDebug { "Hide Comment" }
            it.result = it.args[0]
        }
    }
}