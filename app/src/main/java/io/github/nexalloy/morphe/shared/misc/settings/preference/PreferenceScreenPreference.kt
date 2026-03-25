@file:Suppress("DEPRECATION")

package io.github.nexalloy.morphe.shared.misc.settings.preference

import android.content.Context
import android.preference.Preference
import android.preference.PreferenceManager
import android.preference.PreferenceScreen
import app.morphe.extension.shared.Logger

@Suppress("MemberVisibilityCanBePrivate")
open class PreferenceScreenPreference(
    key: String? = null,
    titleKey: String = "${key}_title",
    summaryKey: String? = "${key}_summary",
    icon: String? = null,
    iconBold: String? = null,
    layout: String? = null,
    sorting: Sorting = Sorting.BY_TITLE,
    tag: Class<out Preference> = PreferenceScreen::class.java,
    val preferences: Set<BasePreference>,
    // Alternatively, instead of repurposing the key for sorting,
    // an extra bundle parameter can be added to the preferences XML declaration.
    // This would require bundling and referencing an additional XML file
    // or adding new attributes to the attrs.xml file.
    // Since the key value is not currently used by the extensions,
    // for now it's much simpler to modify the key to include the sort parameter.
) : BasePreference(sorting.appendSortType(key), titleKey, summaryKey, icon, iconBold, layout, tag) {

    lateinit var children: List<Preference>
    lateinit var preferenceScreen: PreferenceScreen
    override fun build(ctx: Context, prefMgr: PreferenceManager): Preference {
        Logger.printDebug { "build $key" }
        return prefMgr.createPreferenceScreen(ctx).apply {
            applyBaseAttrs(this)
            preferenceScreen = this
            children = preferences.map { it.build(ctx, prefMgr) }
        }
    }

    override fun onAttachedToHierarchy() {
        preferenceScreen.apply {
            children.forEach { addPreference(it) }
            preferences.forEach { it.onAttachedToHierarchy() }
        }
    }

    /**
     * How a PreferenceScreen should be sorted.
     */
    enum class Sorting(val keySuffix: String) {
        /**
         * Sort by the localized preference title.
         */
        BY_TITLE("_sort_by_title"),

        /**
         * Sort by the preference keys.
         */
        BY_KEY("_sort_by_key"),

        /**
         * Unspecified sorting.
         */
        UNSORTED("_sort_by_unsorted");

        /**
         * @return The key with this sort type appended to to the end,
         *         or if key is null then null is returned.
         */
        fun appendSortType(key: String?): String? {
            if (key == null) return null
            if (this == UNSORTED) return key
            return key + keySuffix
        }
    }
}
