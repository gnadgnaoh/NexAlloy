package io.github.nexalloy.revanced.zalo.adtima

import io.github.nexalloy.morphe.fingerprint
import io.github.nexalloy.revanced.zalo.AdtimaClasses

val zAdsNativeLoadAdsFingerprint = fingerprint {
    definingClass(AdtimaClasses.ZADS_NATIVE)
    name("loadAds")
    parameters("Ljava/lang/String;")
}

val zAdsBundlePreloadFingerprint = fingerprint {
    definingClass(AdtimaClasses.ZADS_BUNDLE)
    name("preloadAds")
    parameters("Ljava/lang/String;")
}

val zAdsTrackingInventoryFingerprint = fingerprint {
    definingClass(AdtimaClasses.ZADS_TRACKING)
    name("haveAdsInventory")
    parameters("Ljava/lang/String;")
}
