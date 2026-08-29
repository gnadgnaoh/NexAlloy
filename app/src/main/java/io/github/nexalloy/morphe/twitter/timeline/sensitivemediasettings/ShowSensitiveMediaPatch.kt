package io.github.nexalloy.morphe.twitter.timeline.sensitivemediasettings

import io.github.nexalloy.morphe.twitter.timeline.tweetInfoHook.TweetInfoHook
import io.github.nexalloy.morphe.twitter.timeline.tweetInfoHook.showSensitiveMediaEnabled
import io.github.nexalloy.patch

val ShowSensitiveMedia = patch(
    name = "Show sensitive media",
    description = "Disables the sensitive media warning/blur on posts.",
) {
    dependsOn(TweetInfoHook)
    showSensitiveMediaEnabled = true

    MediaVisibilityResultsConstructorFingerprint.hookMethod {
        before { param -> param.args[0] = null }
    }
}
