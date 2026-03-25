@file:Suppress("DEPRECATION")

package io.github.nexalloy.morphe.shared.misc.settings.preference

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.preference.Preference
import android.preference.PreferenceManager

class IntentPreference(
    key: String? = null,
    titleKey: String = "${key}_title",
    summaryKey: String? = "${key}_summary",
    icon: String? = null,
    iconBold: String? = null,
    layout: String? = null,
    tag: Class<out Preference> = Preference::class.java,
    val intent: Intent,
) : BasePreference(key, titleKey, summaryKey, icon, iconBold, layout, tag) {
    override fun build(ctx: Context, prefMgr: PreferenceManager): Preference {
        return super.build(ctx, prefMgr).also { pref ->
            pref.intent = Intent().apply {
                component = ComponentName(intent.targetPackageSupplier(), intent.targetClass)
                putExtra("data", intent.data)
            }
        }
    }

    data class Intent(
        internal val data: String,
        internal val targetClass: String,
        internal val targetPackageSupplier: () -> String,
    )
}