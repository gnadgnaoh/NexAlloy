@file:Suppress("DEPRECATION", "DiscouragedApi")

package io.github.nexalloy.morphe.shared.misc.settings.preference

import android.content.Context
import android.preference.Preference
import android.preference.PreferenceManager
import android.preference.SwitchPreference

@Suppress("MemberVisibilityCanBePrivate")
class SwitchPreference(
    key: String? = null,
    titleKey: String = "${key}_title",
    tag: Class<out Preference> = SwitchPreference::class.java,
    icon: String? = null,
    iconBold: String? = null,
    layout: String? = null,
    val summaryOnKey: String = "${key}_summary_on",
    val summaryOffKey: String = "${key}_summary_off"
) : BasePreference(key, titleKey, null, icon, iconBold, layout, tag) {
    override fun build(ctx: Context, prefMgr: PreferenceManager): Preference {
        return SwitchPreference(ctx).apply {
            applyBaseAttrs(this)
            trySetString(summaryOnKey) { summaryOn = it }
            trySetString(summaryOffKey) { summaryOff = it }
        }
    }
}