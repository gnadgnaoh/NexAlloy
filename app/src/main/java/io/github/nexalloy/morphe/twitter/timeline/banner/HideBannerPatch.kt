package io.github.nexalloy.morphe.twitter.timeline.banner

import io.github.nexalloy.patch

/**
 * Confirmed via DEX analysis: this class has exactly one method
 * returning boolean (named "i"), so class + return-type is sufficient
 * to uniquely match it. The method's first branch reads field "k" via
 * IF_NEZ to decide whether to even consider showing the banner; we
 * short-circuit before any of that (and its UI side effects like
 * NewItemBannerView.d(true)) can run.
 */
val HideBanner = patch(
    name = "Hide Banner",
    description = "Hides the \"new posts\" banner shown at the top of the timeline.",
) {
    HideBannerFingerprint.hookMethod {
        before { param -> param.result = false }
    }
}
