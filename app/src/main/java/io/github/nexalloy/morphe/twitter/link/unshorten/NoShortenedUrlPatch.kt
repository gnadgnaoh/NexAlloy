package io.github.nexalloy.morphe.twitter.link.unshorten

import io.github.nexalloy.patch

val NoShortenedUrl = patch(
    name = "No shortened URL",
    description = "Gets rid of t.co short urls by showing the expanded URL instead.",
) {
    JsonUrlEntityObjectMapperFingerprint.hookMethod {
        after { param ->
            param.result = unshortJsonUrlEntity(param.result)
        }
    }
}
