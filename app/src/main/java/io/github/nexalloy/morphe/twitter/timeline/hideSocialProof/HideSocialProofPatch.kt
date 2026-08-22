package io.github.nexalloy.morphe.twitter.timeline.hideSocialProof

import io.github.nexalloy.patch

val HideSocialProof = patch(
    name = "Hide followed by context",
    description = "Hides the \"followed by\" context under a profile.",
) {
    SetSocialProofDataFingerprint.hookMethod {
        before { param -> param.result = Unit }
    }
}
