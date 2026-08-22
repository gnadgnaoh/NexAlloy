package io.github.nexalloy.morphe.twitter.timeline.hideNavbarBadges

import io.github.nexalloy.patch

val HideNavBarBadges = patch(
    name = "Hide badges from navigation bar icons",
    description = "Hides notification nudges & counts from navigation bar icons.",
) {
    SetBadgeNumberFingerprint.hookMethod {
        before { param -> param.args[0] = 0 }
    }
}
