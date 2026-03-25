package io.github.nexalloy.morphe.reddit.misc.tracking.url

import io.github.nexalloy.patch

val SanitizeUrlQuery = patch(
    name = "Sanitize sharing links",
    description = "Removes the tracking query parameters from shared links."
) {
    ::shareLinkFormatterFingerprint.hookMethod {
        before {
            it.result = it.args[0]
        }
    }
}