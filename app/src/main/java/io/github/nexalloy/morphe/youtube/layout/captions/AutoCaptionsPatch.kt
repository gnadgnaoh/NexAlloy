package io.github.nexalloy.morphe.youtube.layout.captions

import app.morphe.extension.youtube.patches.AutoCaptionsPatch
import io.github.nexalloy.patch
import io.github.nexalloy.morphe.shared.misc.settings.preference.ListPreference
import io.github.nexalloy.morphe.youtube.misc.playservice.VersionCheck
import io.github.nexalloy.morphe.youtube.misc.playservice.is_20_26_or_greater
import io.github.nexalloy.morphe.youtube.misc.settings.PreferenceScreen
import io.github.nexalloy.morphe.youtube.video.information.onCreateHook

val AutoCaptions = patch(
    name = "Auto captions",
    description = "Adds an option to disable captions from being automatically enabled.",
) {
    dependsOn(VersionCheck)

    PreferenceScreen.PLAYER.addPreferences(
        if (is_20_26_or_greater) {
            ListPreference("morphe_auto_captions_style")
        } else {
            ListPreference(
                key = "morphe_auto_captions_style",
                entriesKey = "morphe_auto_captions_style_legacy_entries",
                entryValuesKey = "morphe_auto_captions_style_legacy_entry_values"
            )
        }
    )

    // TODO disableAutoCaptions — SubtitleManagerFingerprint METHOD_MID

    onCreateHook.add { AutoCaptionsPatch.newVideoStarted(it) }

    StartVideoInformerFingerprint.hookMethod {
        before { AutoCaptionsPatch.videoInformationLoaded() }
    }

    // Disable mute auto captions feature flag.
    if (is_20_26_or_greater) {
        // NoVolumeCaptionsFeatureFlagFingerprint
        NoVolumeCaptionsFeatureFlagFingerprint.hookMethod {
            before {
                it.result = AutoCaptionsPatch.disableMuteAutoCaptions()
            }
        }
    }
}
