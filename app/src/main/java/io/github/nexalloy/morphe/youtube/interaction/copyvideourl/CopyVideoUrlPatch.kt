package io.github.nexalloy.morphe.youtube.interaction.copyvideourl

import app.morphe.extension.youtube.videoplayer.CopyVideoURLButton
import io.github.nexalloy.R
import io.github.nexalloy.morphe.shared.misc.settings.preference.PreferenceCategory
import io.github.nexalloy.morphe.shared.misc.settings.preference.PreferenceScreenPreference.Sorting
import io.github.nexalloy.morphe.shared.misc.settings.preference.SwitchPreference
import io.github.nexalloy.morphe.youtube.layout.player.buttons.addPlayerBottomButton
import io.github.nexalloy.morphe.youtube.layout.player.buttons.playerOverlayButtonsHook
import io.github.nexalloy.morphe.youtube.misc.playercontrols.ControlInitializer
import io.github.nexalloy.morphe.youtube.misc.playercontrols.LegacyPlayerControls
import io.github.nexalloy.morphe.youtube.misc.playercontrols.addLegacyBottomControl
import io.github.nexalloy.morphe.youtube.misc.playercontrols.initializeLegacyBottomControl
import io.github.nexalloy.morphe.youtube.misc.settings.PreferenceScreen
import io.github.nexalloy.morphe.youtube.video.information.VideoInformationPatch
import io.github.nexalloy.patch

val CopyVideoUrl = patch(
    name = "Copy video URL",
    description = "Adds options to display buttons in the video player to copy video URLs.",
) {
    dependsOn(
        LegacyPlayerControls,
        playerOverlayButtonsHook,
        VideoInformationPatch,
    )

    PreferenceScreen.PLAYER.addPreferences(
        PreferenceCategory(
            titleKey = null,
            sorting = Sorting.UNSORTED,
            tag = app.morphe.extension.shared.settings.preference.NoTitlePreferenceCategory::class.java,
            preferences = setOf(
                SwitchPreference("morphe_copy_video_url"),
                SwitchPreference("morphe_copy_video_url_timestamp")
            )
        )
    )

    addPlayerBottomButton(CopyVideoURLButton::initializeButton)

    addLegacyBottomControl(R.layout.morphe_copy_video_url_button)
    initializeLegacyBottomControl(
        ControlInitializer(
            R.id.morphe_copy_video_url_button,
            CopyVideoURLButton::initializeLegacyButton,
            CopyVideoURLButton::setVisibility,
            CopyVideoURLButton::setVisibilityImmediate,
            CopyVideoURLButton::setVisibilityNegatedImmediate
        )
    )
}