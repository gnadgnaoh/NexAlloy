package io.github.nexalloy.morphe.twitter.misc.blockRedirectToXLite

import io.github.nexalloy.morphe.Fingerprint

internal object RedirectingToXLiteFlagCheckFingerprint : Fingerprint(
    returnType = "Z",
    strings = listOf(
        "x_lite_in_tfa_for_existing_users_enabled",
        "existing_user_redirected_to_x_lite",
        "x_lite_in_tfa_for_existing_users_exit_enabled",
    ),
)
