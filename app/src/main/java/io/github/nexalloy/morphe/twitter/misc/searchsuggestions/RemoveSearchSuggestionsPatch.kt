package io.github.nexalloy.morphe.twitter.misc.searchsuggestions

import io.github.nexalloy.patch

val RemoveSearchSuggestions = patch(
    name = "Remove search suggestions",
    description = "Hides/removes search suggestions in the explore section.",
) {
    SearchSuggestionFingerprint.hookMethod {
        after { param -> param.result = null }
    }
}
