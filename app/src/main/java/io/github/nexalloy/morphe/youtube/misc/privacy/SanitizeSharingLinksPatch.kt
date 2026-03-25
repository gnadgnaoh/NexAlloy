package io.github.nexalloy.morphe.youtube.misc.privacy

import io.github.nexalloy.patch
import io.github.nexalloy.morphe.shared.misc.privacy.SanitizeSharingLinks
import io.github.nexalloy.morphe.youtube.misc.settings.PreferenceScreen

val SanitizeSharingLinks = patch(
    name = "Sanitize sharing links",
    description = "Removes the tracking query parameters from shared links."
) {
    SanitizeSharingLinks(preferenceScreen = PreferenceScreen.MISC)
}