package io.github.nexalloy.morphe.twitter.timeline.enableVidAutoAdvance

import io.github.nexalloy.morphe.Fingerprint

internal object PersistentVideoSettingsToStringFingerprint : Fingerprint(
    name = "toString",
    strings = listOf("PersistentVideoSettings(hasLockedPlaybackSpeed="),
)

internal object PersistentVideoSettingsConstructorFingerprint : Fingerprint(
    classFingerprint = PersistentVideoSettingsToStringFingerprint,
    name = "<init>",
    custom = { paramCount = 8 },
)

internal object PlayerConfigurationToStringFingerprint : Fingerprint(
    name = "toString",
    strings = listOf("PlayerConfiguration(videoScale="),
)

internal object PlayerConfigurationConstructorFingerprint : Fingerprint(
    classFingerprint = PlayerConfigurationToStringFingerprint,
    name = "<init>",
    custom = { paramCount = 14 },
)
