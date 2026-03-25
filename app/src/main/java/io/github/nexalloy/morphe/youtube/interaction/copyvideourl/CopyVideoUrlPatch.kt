package io.github.nexalloy.morphe.youtube.interaction.copyvideourl

import app.morphe.extension.youtube.videoplayer.CopyVideoURLButton
import app.morphe.extension.youtube.videoplayer.CopyVideoURLTimestampButton
import io.github.nexalloy.R
import io.github.nexalloy.patch
import io.github.nexalloy.morphe.shared.misc.settings.preference.SwitchPreference
import io.github.nexalloy.morphe.youtube.misc.playercontrols.ControlInitializer
import io.github.nexalloy.morphe.youtube.misc.playercontrols.PlayerControls
import io.github.nexalloy.morphe.youtube.misc.playercontrols.addBottomControl
import io.github.nexalloy.morphe.youtube.misc.playercontrols.initializeBottomControl
import io.github.nexalloy.morphe.youtube.misc.settings.PreferenceScreen
import io.github.nexalloy.morphe.youtube.video.information.VideoInformationPatch

val CopyVideoUrl = patch(
    name = "Copy video URL",
    description = "Adds options to display buttons in the video player to copy video URLs.",
) {
    dependsOn(
        PlayerControls,
        VideoInformationPatch,
    )

    PreferenceScreen.PLAYER.addPreferences(
        SwitchPreference("morphe_copy_video_url"),
        SwitchPreference("morphe_copy_video_url_timestamp"),
    )

    addBottomControl(R.layout.morphe_copy_video_url_button)
    initializeBottomControl(
        ControlInitializer(
            R.id.morphe_copy_video_url_timestamp_button,
            CopyVideoURLTimestampButton::initializeButton,
            CopyVideoURLTimestampButton::setVisibility,
            CopyVideoURLTimestampButton::setVisibilityImmediate,
            CopyVideoURLTimestampButton::setVisibilityNegatedImmediate
        )
    )
    initializeBottomControl(
        ControlInitializer(
            R.id.morphe_copy_video_url_button,
            CopyVideoURLButton::initializeButton,
            CopyVideoURLButton::setVisibility,
            CopyVideoURLButton::setVisibilityImmediate,
            CopyVideoURLButton::setVisibilityNegatedImmediate
        )
    )
}