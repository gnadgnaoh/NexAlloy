package io.github.nexalloy.morphe.twitter.timeline.forceHD

import io.github.nexalloy.morphe.AccessFlags
import io.github.nexalloy.morphe.Fingerprint

internal object PlayerSupportFingerprint : Fingerprint(
    accessFlags = listOf(AccessFlags.PUBLIC, AccessFlags.STATIC),
    custom = { paramCount = 2 }
) {
    init {
        classMatcher { className("av.player.support", org.luckypray.dexkit.query.enums.StringMatchType.Contains) }
    }
}

