package io.github.nexalloy.morphe.music.layout.premium

import android.view.View
import app.morphe.extension.music.patches.HideGetPremiumPatch
import app.morphe.extension.shared.Logger
import app.morphe.extension.shared.ResourceUtils
import io.github.nexalloy.patch
import io.github.nexalloy.morphe.music.misc.settings.PreferenceScreen
import io.github.nexalloy.morphe.shared.misc.settings.preference.SwitchPreference

val HideGetPremium = patch(
    name = "Hide 'Get Music Premium'",
    description = "Adds an option to hide the \"Get Music Premium\" label in the settings and account menu.",
) {
    PreferenceScreen.ADS.addPreferences(
        SwitchPreference("morphe_music_hide_get_premium_label"),
    )

    ::hideGetPremiumFingerprint.hookMethod {
        val id = ResourceUtils.getIdIdentifier("unlimited_panel")
        after { param ->
            val thiz = param.thisObject
            for (field in thiz.javaClass.fields) {
                val view = field.get(thiz)
                if (view !is View) continue
                val panelView = view.findViewById<View>(id) ?: continue
                Logger.printDebug { "hide get premium" }
                panelView.visibility = View.GONE
                break
            }
        }
    }

    ::membershipSettingsFingerprint.hookMethod {
        before {
            if (HideGetPremiumPatch.hideGetPremiumLabel()) it.result = null
        }
    }
}