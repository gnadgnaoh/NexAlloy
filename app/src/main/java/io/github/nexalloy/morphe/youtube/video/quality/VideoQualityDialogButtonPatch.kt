package io.github.nexalloy.morphe.youtube.video.quality

import app.morphe.extension.youtube.videoplayer.VideoQualityDialogButton
import io.github.nexalloy.R
import io.github.nexalloy.patch
import io.github.nexalloy.morphe.shared.misc.settings.preference.SwitchPreference
import io.github.nexalloy.morphe.youtube.misc.playercontrols.ControlInitializer
import io.github.nexalloy.morphe.youtube.misc.playercontrols.PlayerControls
import io.github.nexalloy.morphe.youtube.misc.playercontrols.addBottomControl
import io.github.nexalloy.morphe.youtube.misc.playercontrols.initializeBottomControl
import io.github.nexalloy.morphe.youtube.misc.settings.PreferenceScreen

val VideoQualityDialogButtonPatch = patch(
    description = "Adds the option to display video quality dialog button in the video player.",
) {
    dependsOn(
        RememberVideoQuality,
        PlayerControls,
    )

    PreferenceScreen.PLAYER.addPreferences(
        SwitchPreference("morphe_video_quality_dialog_button"),
    )

    addBottomControl(R.layout.morphe_video_quality_dialog_button_container)
    initializeBottomControl(
        ControlInitializer(
            R.id.morphe_video_quality_dialog_button_container,
            VideoQualityDialogButton::initializeButton,
            VideoQualityDialogButton::setVisibility,
            VideoQualityDialogButton::setVisibilityImmediate,
            VideoQualityDialogButton::setVisibilityNegatedImmediate
        )
    )
}
