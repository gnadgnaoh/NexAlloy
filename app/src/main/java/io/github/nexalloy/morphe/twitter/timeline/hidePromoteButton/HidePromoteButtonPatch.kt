package io.github.nexalloy.morphe.twitter.timeline.hidePromoteButton

import io.github.nexalloy.morphe.twitter.timeline.tweetInfoHook.TweetInfoHook
import io.github.nexalloy.morphe.twitter.timeline.tweetInfoHook.hidePromoteButtonEnabled
import io.github.nexalloy.patch

val HidePromoteButton = patch(
    name = "Hide promote button",
    description = "Hides the promote button shown under your own posts.",
) {
    dependsOn(TweetInfoHook)
    hidePromoteButtonEnabled = true
}
