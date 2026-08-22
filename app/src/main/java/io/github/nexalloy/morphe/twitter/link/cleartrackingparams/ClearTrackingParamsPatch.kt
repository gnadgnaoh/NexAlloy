package io.github.nexalloy.morphe.twitter.link.cleartrackingparams

import io.github.nexalloy.patch

val ClearTrackingParams = patch(
    name = "Clear tracking params",
    description = "Removes tracking parameters when sharing links.",
) {
    AddSessionTokenFingerprint.hookMethod {
        before { param ->
            param.result = param.args.getOrNull(0)
        }
    }
}
