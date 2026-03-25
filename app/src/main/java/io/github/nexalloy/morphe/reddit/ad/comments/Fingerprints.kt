package io.github.nexalloy.morphe.reddit.ad.comments

import io.github.nexalloy.morphe.fingerprint
import org.luckypray.dexkit.query.enums.StringMatchType

val hideCommentAdsFingerprint = fingerprint {
    methodMatcher {
        name("invokeSuspend")
        declaredClass("LoadAdsCombinedCall", StringMatchType.Contains)
    }
}