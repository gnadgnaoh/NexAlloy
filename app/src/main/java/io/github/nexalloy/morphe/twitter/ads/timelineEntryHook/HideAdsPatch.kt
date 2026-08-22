package io.github.nexalloy.morphe.twitter.ads.timelineEntryHook

import io.github.nexalloy.morphe.twitter.featureFlag.featureFlagPatch.FeatureFlagHook
import io.github.nexalloy.morphe.twitter.featureFlag.featureFlagPatch.featureFlagOverrides
import io.github.nexalloy.patch

val HideAds = patch(
    name = "Remove ads",
    description = "Removes promoted posts, trends and Google ads.",
) {
    dependsOn(TimelineEntryHook, FeatureFlagHook)
    hideAdsEnabled = true
    featureFlagOverrides["ssp_ads_dsp_client_context_enabled"] = false
}
