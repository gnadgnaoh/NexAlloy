package io.github.nexalloy.morphe.youtube.interaction.swipecontrols

import app.morphe.extension.shared.settings.preference.ColorPickerWithOpacitySliderPreference
import app.morphe.extension.youtube.swipecontrols.SwipeControlsHostActivity
import io.github.nexalloy.patch
import io.github.nexalloy.morphe.shared.misc.settings.preference.InputType
import io.github.nexalloy.morphe.shared.misc.settings.preference.ListPreference
import io.github.nexalloy.morphe.shared.misc.settings.preference.SwitchPreference
import io.github.nexalloy.morphe.shared.misc.settings.preference.TextPreference
import io.github.nexalloy.morphe.youtube.misc.playertype.PlayerTypeHook
import io.github.nexalloy.morphe.youtube.misc.settings.PreferenceScreen
import io.github.nexalloy.morphe.youtube.shared.mainActivityClass

val SwipeControls = patch(
    name = "Swipe controls",
    description = "Adds options to enable and configure volume and brightness swipe controls.",
) {
    dependsOn(
        PlayerTypeHook,
    )

//    if (is_19_43_or_greater) {
//        PreferenceScreen.SWIPE_CONTROLS.addPreferences(
//            SwitchPreference("morphe_swipe_change_video")
//        )
//    }

    PreferenceScreen.SWIPE_CONTROLS.addPreferences(
        SwitchPreference("morphe_swipe_brightness"),
        SwitchPreference("morphe_swipe_volume"),
        SwitchPreference("morphe_swipe_press_to_engage"),
        SwitchPreference("morphe_swipe_haptic_feedback"),
        SwitchPreference("morphe_swipe_save_and_restore_brightness"),
        SwitchPreference("morphe_swipe_lowest_value_enable_auto_brightness"),
        ListPreference("morphe_swipe_overlay_style"),
        TextPreference("morphe_swipe_overlay_background_opacity", inputType = InputType.NUMBER),
        TextPreference(
            "morphe_swipe_overlay_progress_brightness_color",
            tag = ColorPickerWithOpacitySliderPreference::class.java,
            inputType = InputType.TEXT_CAP_CHARACTERS
        ),
        TextPreference(
            "morphe_swipe_overlay_progress_volume_color",
            tag = ColorPickerWithOpacitySliderPreference::class.java,
            inputType = InputType.TEXT_CAP_CHARACTERS
        ),
        TextPreference("morphe_swipe_text_overlay_size", inputType = InputType.NUMBER),
        TextPreference("morphe_swipe_overlay_timeout", inputType = InputType.NUMBER),
        TextPreference("morphe_swipe_threshold", inputType = InputType.NUMBER),
        TextPreference("morphe_swipe_volume_sensitivity", inputType = InputType.NUMBER),
    )

    SwipeControlsHostActivity.hookActivity(::mainActivityClass.clazz)

    // TODO patch to enable/disable swipe to change video.
}