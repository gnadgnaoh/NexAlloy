package io.github.nexalloy.morphe.youtube.misc.backgroundplayback

import app.morphe.extension.youtube.patches.BackgroundPlaybackPatch
import de.robv.android.xposed.XC_MethodReplacement.returnConstant
import io.github.nexalloy.patch
import io.github.nexalloy.morphe.shared.misc.settings.preference.SwitchPreference
import io.github.nexalloy.morphe.youtube.misc.litho.filter.featureFlagCheck
import io.github.nexalloy.morphe.youtube.misc.settings.PreferenceScreen

val BackgroundPlayback = patch(
    name = "Remove background playback restrictions",
    description = "Removes restrictions on background playback, including playing kids videos in the background.",
) {

    PreferenceScreen.SHORTS.addPreferences(
        SwitchPreference("morphe_shorts_disable_background_playback"),
    )

    BackgroundPlaybackManagerFingerprint.hookMethod {
        after {
            it.result = BackgroundPlaybackPatch.isBackgroundPlaybackAllowed(it.result as Boolean)
        }
    }

    ::backgroundPlaybackManagerShortsFingerprint.dexMethodList.forEach {
        it.hookMethod {
            after {
                it.result =
                    BackgroundPlaybackPatch.isBackgroundShortsPlaybackAllowed(it.result as Boolean)
            }
        }
    }

    // Enable background playback option in YouTube settings
    ::backgroundPlaybackSettingsSubFingerprint.hookMethod(returnConstant(true))

    // Force allowing background play for Shorts.
    ShortsBackgroundPlaybackFeatureFlagFingerprint.hookMethod(returnConstant(true))

    // Force allowing background play for videos labeled for kids.
    KidsBackgroundPlaybackPolicyControllerFingerprint.hookMethod(returnConstant(Unit))

    // Fix PiP buttons not working after locking/unlocking device screen.
    // Starts with 19.34.xx
    ::featureFlagCheck.hookMethod {
        before { if (it.args[0] == PIP_INPUT_CONSUMER_FEATURE_FLAG) it.result = false }
    }

    // Client flag that interferes with background playback of some video types.
    // Exact purpose is not clear and it's used in ~ 100 locations.
    // Starts with 20.29.xx
    ::featureFlagCheck.hookMethod {
        before {
            if (it.args[0] == 45698813L)
                it.result = false
        }
    }
}