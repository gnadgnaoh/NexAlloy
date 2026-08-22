package io.github.nexalloy.morphe.twitter.timeline.tweetInfoHook

import io.github.nexalloy.morphe.Fingerprint

internal object TweetInfoHookFingerprint : Fingerprint(
    definingClass = "Lcom/twitter/api/model/json/core/JsonApiTweet\$\$JsonObjectMapper;",
    name = "parse",
    returnType = "Ljava/lang/Object;",
)
