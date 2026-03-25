package io.github.nexalloy.morphe.youtube.video.speed.button

import app.morphe.extension.youtube.videoplayer.PlaybackSpeedDialogButton
import io.github.nexalloy.R
import io.github.nexalloy.patch
import io.github.nexalloy.morphe.shared.misc.settings.preference.SwitchPreference
import io.github.nexalloy.morphe.youtube.misc.playercontrols.ControlInitializer
import io.github.nexalloy.morphe.youtube.misc.playercontrols.PlayerControls
import io.github.nexalloy.morphe.youtube.misc.playercontrols.addBottomControl
import io.github.nexalloy.morphe.youtube.misc.playercontrols.initializeBottomControl
import io.github.nexalloy.morphe.youtube.misc.settings.PreferenceScreen
import io.github.nexalloy.morphe.youtube.video.information.VideoInformationPatch
import io.github.nexalloy.morphe.youtube.video.information.userSelectedPlaybackSpeedHook
import io.github.nexalloy.morphe.youtube.video.information.videoSpeedChangedHook
import io.github.nexalloy.morphe.youtube.video.speed.custom.CustomPlaybackSpeed

val PlaybackSpeedButton = patch(
    description = "Adds the option to display playback speed dialog button in the video player.",
) {
    dependsOn(
        CustomPlaybackSpeed,
        PlayerControls,
        VideoInformationPatch,
    )

    PreferenceScreen.PLAYER.addPreferences(
        SwitchPreference("morphe_playback_speed_dialog_button"),
    )

    addBottomControl(R.layout.morphe_playback_speed_dialog_button)
    initializeBottomControl(
        ControlInitializer(
            R.id.morphe_playback_speed_dialog_button_container,
            PlaybackSpeedDialogButton::initializeButton,
            PlaybackSpeedDialogButton::setVisibility,
            PlaybackSpeedDialogButton::setVisibilityImmediate,
            PlaybackSpeedDialogButton::setVisibilityNegatedImmediate,
        )
    )

    videoSpeedChangedHook.add { PlaybackSpeedDialogButton.videoSpeedChanged(it) }
    userSelectedPlaybackSpeedHook.add { PlaybackSpeedDialogButton.videoSpeedChanged(it) }
}

