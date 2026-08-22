package io.github.nexalloy.revanced.zalo.tracking

import io.github.nexalloy.patch
import io.github.nexalloy.revanced.zalo.adtima.zAdsTrackingInventoryFingerprint

val DisableAdsTracking = patch(
    name = "Disable ads tracking",
    description = "Blocks Adtima impression/click reporting and the Zalo ads tracking receiver.",
) {
    ::zAdsTrackingInventoryFingerprint.hookMethod {
        before { param -> param.result = null }
    }

    ::adsTrackingReceiverFingerprint.hookMethod {
        before { param -> param.result = null }
    }
}
