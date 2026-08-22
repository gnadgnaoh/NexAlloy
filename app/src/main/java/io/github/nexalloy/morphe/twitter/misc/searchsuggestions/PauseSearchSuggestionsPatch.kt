package io.github.nexalloy.morphe.twitter.misc.searchsuggestions

import io.github.nexalloy.patch

val PauseSearchSuggestions = patch(
    name = "Pause search suggestions",
    description = "Search suggestions will not be saved locally.",
) {
    SearchDbInsertFingerprint.hookMethod {
        before { param -> param.result = Unit }
    }
}
