package io.github.nexalloy.revanced.zalo.adtima

import io.github.nexalloy.patch

val DisableAdtimaAdRequests = patch(
    name = "Block Adtima ad requests",
    description = "Stops the bundled Adtima SDK from fetching ad creatives " +
            "(api.adtimaserver.vn/mobad/*).",
) {
    ::zAdsNativeLoadAdsFingerprint.hookMethod {
        before { param -> param.result = null }
    }
    ::zAdsBundlePreloadFingerprint.hookMethod {
        before { param -> param.result = null }
    }
}
