package io.github.nexalloy.morphe.twitter.link.unshorten

import io.github.nexalloy.patch

val NoShortenedUrl = patch(
    name = "No shortened URL",
    description = "Gets rid of t.co short urls by showing the expanded URL instead.",
) {
    UrlEntityConstructorFingerprint.hookMethod {
        before { param ->
            unshortenArgs(param, displayIdx = 1, expandedIdx = 3, urlIdx = 4)
        }
    }

    UrlEntitySerialConstructorFingerprint.hookMethod {
        before { param ->
            unshortenArgs(param, displayIdx = 3, expandedIdx = 4, urlIdx = 5)
        }
    }
}
