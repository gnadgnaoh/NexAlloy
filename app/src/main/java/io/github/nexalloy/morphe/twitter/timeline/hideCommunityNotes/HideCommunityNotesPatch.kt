package io.github.nexalloy.morphe.twitter.timeline.hideCommunityNotes

import io.github.nexalloy.morphe.twitter.timeline.tweetInfoHook.TweetInfoHook
import io.github.nexalloy.morphe.twitter.timeline.tweetInfoHook.hideCommunityNotesEnabled
import io.github.nexalloy.patch

val HideCommunityNotes = patch(
    name = "Hide Community Notes",
    description = "Hides Community Notes shown under posts.",
) {
    dependsOn(TweetInfoHook)
    hideCommunityNotesEnabled = true
}
