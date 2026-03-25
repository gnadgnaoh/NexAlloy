package io.github.nexalloy.morphe.shared.misc.debugging

import app.morphe.extension.shared.patches.EnableDebuggingPatch
import app.morphe.extension.shared.settings.preference.ClearLogBufferPreference
import app.morphe.extension.shared.settings.preference.ExportLogToClipboardPreference
import app.morphe.extension.shared.settings.preference.FeatureFlagsManagerPreference
import io.github.nexalloy.PatchExecutor
import io.github.nexalloy.hookMethod
import io.github.nexalloy.morphe.shared.misc.settings.preference.BasePreference
import io.github.nexalloy.morphe.shared.misc.settings.preference.BasePreferenceScreen
import io.github.nexalloy.morphe.shared.misc.settings.preference.NonInteractivePreference
import io.github.nexalloy.morphe.shared.misc.settings.preference.PreferenceScreenPreference
import io.github.nexalloy.morphe.shared.misc.settings.preference.PreferenceScreenPreference.Sorting
import io.github.nexalloy.morphe.shared.misc.settings.preference.SwitchPreference

fun PatchExecutor.EnableDebugging(
    preferenceScreen: BasePreferenceScreen.Screen,
    additionalDebugPreferences: List<BasePreference> = emptyList()
) {
    val preferences = mutableSetOf<BasePreference>(
        SwitchPreference("morphe_debug"),
    )

    preferences.addAll(additionalDebugPreferences)

    preferences.addAll(
        listOf(
            SwitchPreference("morphe_debug_stacktrace"),
            SwitchPreference("morphe_debug_toast_on_error"),
            NonInteractivePreference(
                "morphe_debug_export_logs_to_clipboard",
                tag = ExportLogToClipboardPreference::class.java,
                selectable = true
            ),
            NonInteractivePreference(
                "morphe_debug_logs_clear_buffer",
                tag = ClearLogBufferPreference::class.java,
                selectable = true
            ),
            NonInteractivePreference(
                "morphe_debug_feature_flags_manager",
                tag = FeatureFlagsManagerPreference::class.java,
                selectable = true
            )
        )
    )

    preferenceScreen.addPreferences(
        PreferenceScreenPreference(
            key = "morphe_debug_screen",
            sorting = Sorting.UNSORTED,
            preferences = preferences,
        )
    )

    // Hook the methods that look up if a feature flag is active.
    ::experimentalBooleanFeatureFlagFingerprint.hookMethod {
        after {
            it.result = EnableDebuggingPatch.isBooleanFeatureFlagEnabled(
                it.result as Boolean,
                it.args[1] as Long
            )
        }
    }

    ::experimentalDoubleFeatureFlagFingerprint.hookMethod {
        after {
            it.result = EnableDebuggingPatch.isDoubleFeatureFlagEnabled(
                it.result as Double,
                it.args[1] as Long,
                it.args[2] as Double
            )
        }
    }

    ::experimentalLongFeatureFlagFingerprint.memberOrNull?.hookMethod {
        after {
            it.result = EnableDebuggingPatch.isLongFeatureFlagEnabled(
                it.result as Long,
                it.args[1] as Long,
                it.args[2] as Long
            )
        }
    }

    ::experimentalStringFeatureFlagFingerprint.memberOrNull?.hookMethod {
        after {
            it.result = EnableDebuggingPatch.isStringFeatureFlagEnabled(
                it.result as String,
                it.args[1] as Long,
                it.args[2] as String
            )
        }
    }

    // There exists other experimental accessor methods for byte[]
    // and wrappers for obfuscated classes, but currently none of those are hooked.
}
