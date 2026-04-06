@file:Suppress("DEPRECATION") @file:SuppressLint("WorldReadableFiles")

package io.github.nexalloy.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.preference.Preference
import android.preference.PreferenceCategory
import android.preference.PreferenceFragment
import android.preference.SwitchPreference
import android.text.format.DateUtils
import android.window.OnBackInvokedDispatcher
import app.morphe.extension.shared.Utils
import app.morphe.extension.shared.settings.preference.about.MorpheAboutPreference
import io.github.nexalloy.AppPatchInfo
import io.github.nexalloy.BuildConfig
import io.github.nexalloy.R
import io.github.nexalloy.appPatchConfigurations
import io.github.nexalloy.common.UpdateChecker
import kotlin.system.exitProcess

class SettingsActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            onBackInvokedDispatcher.registerOnBackInvokedCallback(
                OnBackInvokedDispatcher.PRIORITY_DEFAULT
            ) {
                onBackPressed()
            }
        }
        setContentView(R.layout.activity_settings)
        actionBar?.setDisplayShowHomeEnabled(true)
        if (savedInstanceState != null) return

        fragmentManager.beginTransaction().replace(R.id.settings_container, SettingsFragment())
            .commit()
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        finishAndRemoveTask()
        exitProcess(0)
    }

    class SettingsFragment : PreferenceFragment() {
        fun AppPatchInfo.getPreference(): Preference {
            val preference = Preference(context)
            preference.title = appName
            preference.key = appName
            preference.intent = Intent(context, AppPatchSettingsActivity::class.java).apply {
                putExtra(AppPatchSettingsActivity.ARGUMENT_APP_NAME, appName)
            }
            return preference
        }

        @Deprecated("Deprecated in Java")
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)

            val rootScreen = preferenceManager.createPreferenceScreen(context)
            preferenceScreen = rootScreen

            Preference(context).apply {
                setSummary(R.string.slogan_summary)
                isEnabled = false
                rootScreen.addPreference(this)
            }

            Utils.setContext(context)

            Preference(context).apply {
                summary = "This app uses code from Morphe. To learn more, visit https://morphe.software"
                intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://morphe.software"))
                rootScreen.addPreference(this)
            }

            MorpheAboutPreference(context).apply {
                setTitle(R.string.about_title)
                summary =
                    """Version: ${BuildConfig.VERSION_NAME} (${BuildConfig.COMMIT_HASH}) ${BuildConfig.BUILD_TYPE} 
                       |Build Date: ${DateUtils.getRelativeTimeSpanString(BuildConfig.COMMIT_DATE * 1000)}""".trimMargin()
                rootScreen.addPreference(this)
            }

            Preference(context).apply {
                setTitle(R.string.faq_title)
                intent = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://github.com/NexAlloy/NexAlloy/wiki/Frequently-Asked-Questions")
                )
                rootScreen.addPreference(this)
            }

            addPreferencesFromResource(R.xml.license_prefs)

            Preference(context).apply {
                setTitle(R.string.check_for_update_title)
                setOnPreferenceClickListener {
                    UpdateChecker().apply {
                        setActivity(activity)
                        checkUpdate(silent = false)
                    }
                    true
                }
                rootScreen.addPreference(this)
            }
            UpdateChecker().apply {
                setActivity(activity)
                autoCheckUpdate()
            }

            SwitchPreference(context).apply {
                setTitle(R.string.hide_icon_title)
                setSummary(R.string.hide_icon_summary)
                val aliasName = ComponentName(activity, SettingsActivity::class.java.name + "Alias")
                val packageManager = activity.packageManager

                isChecked =
                    packageManager.getComponentEnabledSetting(aliasName) == PackageManager.COMPONENT_ENABLED_STATE_DISABLED
                setOnPreferenceChangeListener { _, newValue ->
                    val isShow = newValue as Boolean
                    val status =
                        if (isShow) PackageManager.COMPONENT_ENABLED_STATE_DISABLED else PackageManager.COMPONENT_ENABLED_STATE_ENABLED
                    if (packageManager.getComponentEnabledSetting(aliasName) != status) {
                        packageManager.setComponentEnabledSetting(
                            aliasName, status, PackageManager.DONT_KILL_APP
                        )
                    }
                    true
                }
                rootScreen.addPreference(this)
            }

            val isModuleActivated: Boolean = try {
                context.getSharedPreferences("prefs", MODE_WORLD_READABLE)
                true
            } catch (_: SecurityException) {
                false
            }

            if (!isModuleActivated) {
                rootScreen.addPreference(Preference(context).apply {
                    setSummary(R.string.module_not_activated_summary)
                    isEnabled = false
                })
                return
            }

            val patchSelectionCategory = PreferenceCategory(context).apply {
                setTitle(R.string.patch_selection)
                rootScreen.addPreference(this)
            }
            Preference(context).apply {
                setSummary(R.string.force_stop_to_apply_summary)
                isEnabled = false
                patchSelectionCategory.addPreference(this)
            }

            for (appPatchInfo in appPatchConfigurations) {
                patchSelectionCategory.addPreference(appPatchInfo.getPreference())
            }
        }
    }
}
