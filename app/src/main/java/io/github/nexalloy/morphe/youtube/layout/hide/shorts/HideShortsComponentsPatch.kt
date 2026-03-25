package io.github.nexalloy.morphe.youtube.layout.hide.shorts

import app.morphe.extension.youtube.patches.components.ShortsFilter
import io.github.nexalloy.patch
import io.github.nexalloy.morphe.shared.misc.settings.preference.SwitchPreference
import io.github.nexalloy.morphe.youtube.misc.litho.filter.LithoFilter
import io.github.nexalloy.morphe.youtube.misc.litho.filter.addLithoFilter
import io.github.nexalloy.morphe.youtube.misc.settings.PreferenceScreen

val HideShortsComponents = patch(
    name = "Hide Shorts components",
    description = "Adds options to hide components related to Shorts.",
) {
    dependsOn(LithoFilter)

    PreferenceScreen.SHORTS.addPreferences(
        SwitchPreference("morphe_hide_shorts_home"),
        SwitchPreference("morphe_hide_shorts_search"),
        SwitchPreference("morphe_hide_shorts_subscriptions"),
        SwitchPreference("morphe_hide_shorts_history"),
        // TODO: revanced_shorts_player_screen
    )

    addLithoFilter(ShortsFilter())
}