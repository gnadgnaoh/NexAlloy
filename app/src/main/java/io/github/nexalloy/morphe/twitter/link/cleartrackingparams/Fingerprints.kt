package io.github.nexalloy.morphe.twitter.link.cleartrackingparams

import io.github.nexalloy.morphe.Fingerprint

internal object AddSessionTokenFingerprint : Fingerprint(
    parameters = listOf(
        "Ljava/lang/String;",
        "L",
        "Ljava/lang/String;",
    ),
    returnType = "Ljava/lang/String;",
    strings = listOf(
        "<this>",
        "shareParam",
        "sessionToken",
    ),
)
