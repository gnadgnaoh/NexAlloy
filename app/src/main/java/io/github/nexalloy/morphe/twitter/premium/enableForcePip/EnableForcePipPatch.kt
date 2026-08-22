package io.github.nexalloy.morphe.twitter.premium.enableForcePip

import io.github.nexalloy.morphe.twitter.featureFlag.featureFlagPatch.FeatureFlagHook
import io.github.nexalloy.morphe.twitter.featureFlag.featureFlagPatch.featureFlagOverrides
import io.github.nexalloy.patch

val EnableForcePip = patch(
    name = "Enable PiP mode automatically",
    description = "Enables picture-in-picture mode automatically when you close the app while a video is playing.",
) {
    dependsOn(FeatureFlagHook)
    featureFlagOverrides["android_immersive_media_player_native_pip_enabled"] = true
}
