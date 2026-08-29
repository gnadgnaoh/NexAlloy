package io.github.nexalloy.morphe.twitter.timeline.sensitivemediasettings

import io.github.nexalloy.morphe.Fingerprint

internal object MediaVisibilityResultsToStringFingerprint : Fingerprint(
    name = "toString",
    strings = listOf("MediaVisibilityResults(blurImageInterstitial="),
)

internal object MediaVisibilityResultsConstructorFingerprint : Fingerprint(
    classFingerprint = MediaVisibilityResultsToStringFingerprint,
    name = "<init>",
    custom = { paramCount = 1 },
)
