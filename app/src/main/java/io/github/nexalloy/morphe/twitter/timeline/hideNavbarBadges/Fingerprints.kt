package io.github.nexalloy.morphe.twitter.timeline.hideNavbarBadges

import io.github.nexalloy.morphe.Fingerprint
import org.luckypray.dexkit.query.enums.StringMatchType

internal object SetBadgeNumberFingerprint : Fingerprint(
    name = "setBadgeNumber",
) {
    init {
        classMatcher {
            className("BadgeableTabView", StringMatchType.Contains)
        }
    }
}
