package io.github.nexalloy.morphe.twitter.timeline.forceHD

import io.github.nexalloy.morphe.AccessFlags
import io.github.nexalloy.morphe.Fingerprint

internal object BuildMediaItemFingerprint : Fingerprint(
    accessFlags = listOf(AccessFlags.PUBLIC, AccessFlags.STATIC, AccessFlags.FINAL),
    strings = listOf("variants", "x-mpegURL"),
)
