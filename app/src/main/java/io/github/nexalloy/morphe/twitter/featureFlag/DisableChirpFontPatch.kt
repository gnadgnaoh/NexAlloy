package io.github.nexalloy.morphe.twitter.featureFlag

import io.github.nexalloy.morphe.twitter.featureFlag.featureFlagPatch.FeatureFlagHook
import io.github.nexalloy.morphe.twitter.featureFlag.featureFlagPatch.featureFlagOverrides
import io.github.nexalloy.patch

val DisableChirpFont = patch(
    name = "Disable chirp font",
    description = "Forces the internal \"af_ui_chirp_enabled\" feature flag off.",
) {
    dependsOn(FeatureFlagHook)
    featureFlagOverrides["af_ui_chirp_enabled"] = false
}
