package io.github.nexalloy.morphe.youtube.video.speed.remember

import io.github.nexalloy.morphe.findFieldDirect
import io.github.nexalloy.morphe.fingerprint

internal val initializePlaybackSpeedValuesFingerprint = fingerprint {
    parameters("[L", "I")
    strings("menu_item_playback_speed")
}

val onItemClickListenerClassFieldReference = findFieldDirect {
    initializePlaybackSpeedValuesFingerprint().usingFields.first().field
}