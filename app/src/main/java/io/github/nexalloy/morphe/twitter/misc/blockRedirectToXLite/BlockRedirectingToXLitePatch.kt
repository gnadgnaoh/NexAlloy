package io.github.nexalloy.morphe.twitter.misc.blockRedirectToXLite

import io.github.nexalloy.patch

val BlockRedirectingToXLite = patch(
    name = "Block redirecting to X Lite",
    description = "Blocks redirecting to the new X Lite Android UI on launch.",
) {
    RedirectingToXLiteFlagCheckFingerprint.hookMethod {
        after { param -> param.result = false }
    }
}
