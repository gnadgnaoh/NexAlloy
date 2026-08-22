package io.github.nexalloy.morphe.twitter.misc.fab

import io.github.nexalloy.patch

val HideFAB = patch(
    name = "Hide FAB",
    description = "Hides the floating action button (compose tweet button).",
) {
    FabProviderFingerprint.hookMethod {
        after { param -> param.result = null }
    }
}
