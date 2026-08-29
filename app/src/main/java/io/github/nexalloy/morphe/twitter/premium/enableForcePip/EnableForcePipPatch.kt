package io.github.nexalloy.morphe.twitter.premium.enableForcePip

import io.github.nexalloy.morphe.twitter.timeline.enableVidAutoAdvance.PlayerConfigurationConstructorFingerprint
import io.github.nexalloy.patch

private const val ENABLE_PIP_MODE = 8

val EnableForcePip = patch(
    name = "Enable PiP mode automatically",
    description = "Enables picture-in-picture mode automatically when you close the app while a video is playing.",
) {
    PlayerConfigurationConstructorFingerprint.hookMethod {
        before { param -> param.args[ENABLE_PIP_MODE] = true }
    }
}
