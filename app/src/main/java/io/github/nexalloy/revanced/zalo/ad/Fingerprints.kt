package io.github.nexalloy.revanced.zalo.ads

import io.github.nexalloy.morphe.findMethodListDirect
import io.github.nexalloy.morphe.fingerprint
import io.github.nexalloy.revanced.zalo.ZaloFeedKeys

val feedAdsBindFingerprint = fingerprint {
    strings("zinstantMediaType")
    returns("V")
}

val feedAdsLayoutHeightFingerprint = fingerprint {
    classFingerprint(feedAdsBindFingerprint)
    name("getZInstantLayoutHeight")
    returns("I")
}

val feedContentParserFingerprint = fingerprint {
    strings(
        ZaloFeedKeys.TRACK_ADS,
        ZaloFeedKeys.ADS_DATA,
        ZaloFeedKeys.ADS_THUMB,
        ZaloFeedKeys.ADS_ACTION,
        ZaloFeedKeys.TEMPLATE_ID,
    )
    parameters("I", "Lorg/json/JSONObject;")
}

val storyAdsBindFingerprint = fingerprint {
    strings("click_story_ad_cta", "click_name_story_ad", "send_message_story_ad")
    returns("V")
}

val outstreamAdsLayoutFingerprint = fingerprint {
    strings("outstream_ads_close", "outstream_ads_skip", "skip_ads_second")
    returns("V")
}

val adsTemplateLayoutFingerprint = fingerprint {
    strings("cta_ad_show")
    returns("V")
}

val adsNativeLayoutFingerprint = fingerprint {
    name("getStartTimeShow")
    returns("J")
    parameters()
}

val advertisingItemFingerprints = findMethodListDirect {
    findMethod {
        matcher {
            name = "getAdvertisingContent"
            returnType = "com.zing.zalo.shortvideo.domain.entity.content.Content"
            paramCount = 0
        }
    }
}
