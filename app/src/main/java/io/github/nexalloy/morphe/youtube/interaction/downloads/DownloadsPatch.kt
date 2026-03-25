package io.github.nexalloy.morphe.youtube.interaction.downloads

import android.app.Activity
import app.morphe.extension.youtube.patches.DownloadsPatch
import app.morphe.extension.youtube.settings.preference.ExternalDownloaderPreference
import app.morphe.extension.youtube.videoplayer.ExternalDownloadButton
import io.github.nexalloy.R
import io.github.nexalloy.patch
import io.github.nexalloy.morphe.shared.misc.settings.preference.PreferenceScreenPreference
import io.github.nexalloy.morphe.shared.misc.settings.preference.SwitchPreference
import io.github.nexalloy.morphe.shared.misc.settings.preference.TextPreference
import io.github.nexalloy.morphe.youtube.misc.playercontrols.ControlInitializer
import io.github.nexalloy.morphe.youtube.misc.playercontrols.PlayerControls
import io.github.nexalloy.morphe.youtube.misc.playercontrols.addBottomControl
import io.github.nexalloy.morphe.youtube.misc.playercontrols.initializeBottomControl
import io.github.nexalloy.morphe.youtube.misc.settings.PreferenceScreen
import io.github.nexalloy.morphe.youtube.shared.mainActivityOnCreateFingerprint
import io.github.nexalloy.morphe.youtube.video.information.VideoInformationPatch

val Downloads = patch(
    name = "Downloads",
    description = "Adds support to download videos with an external downloader app " +
            "using the in-app download button or a video player action button.",
) {
    dependsOn(
        PlayerControls,
        VideoInformationPatch,
    )

    PreferenceScreen.PLAYER.addPreferences(
        PreferenceScreenPreference(
            key = "morphe_external_downloader_screen",
            sorting = PreferenceScreenPreference.Sorting.UNSORTED,
            preferences = setOf(
                SwitchPreference("morphe_external_downloader"),
                SwitchPreference("morphe_external_downloader_action_button"),
                TextPreference(
                    "morphe_external_downloader_name",
                    tag = ExternalDownloaderPreference::class.java
                ),
            ),
        ),
    )

    addBottomControl(R.layout.morphe_external_download_button)
    initializeBottomControl(
        ControlInitializer(
            R.id.morphe_external_download_button,
            ExternalDownloadButton::initializeButton,
            ExternalDownloadButton::setVisibility,
            ExternalDownloadButton::setVisibilityImmediate,
            ExternalDownloadButton::setVisibilityNegatedImmediate,
        )
    )

    ::mainActivityOnCreateFingerprint.hookMethod {
        before { DownloadsPatch.setMainActivity(it.thisObject as Activity) }
    }

    OfflineVideoEndpointFingerprint.hookMethod {
        before {
            if (DownloadsPatch.inAppDownloadButtonOnClick(it.args[2] as String)) it.result = null
        }
    }
}