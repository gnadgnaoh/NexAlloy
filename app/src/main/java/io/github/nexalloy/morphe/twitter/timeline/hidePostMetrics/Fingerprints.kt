package io.github.nexalloy.morphe.twitter.timeline.hidePostMetrics

import io.github.nexalloy.morphe.Fingerprint

internal object InlineActionViewTextFingerprint : Fingerprint(
    definingClass = "Lcom/twitter/ui/tweet/inlineactions/InlineActionView;",
    returnType = "V",
    parameters = listOf("Ljava/lang/String;", "Z"),
)

internal object TweetStatViewTextFingerprint : Fingerprint(
    returnType = "V",
    parameters = listOf(
        "Lcom/twitter/ui/tweet/TweetStatView;",
        "Ljava/lang/String;",
        "Ljava/lang/String;",
    ),
)
