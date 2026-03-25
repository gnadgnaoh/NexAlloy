package io.github.nexalloy.morphe.youtube.ad.general

import android.view.View
import app.morphe.extension.shared.Logger
import app.morphe.extension.shared.ResourceUtils
import app.morphe.extension.youtube.patches.components.AdsFilter
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedHelpers
import io.github.nexalloy.patch
import io.github.nexalloy.morphe.shared.misc.settings.preference.SwitchPreference
import io.github.nexalloy.morphe.youtube.misc.engagement.addEngagementPanelIdHook
import io.github.nexalloy.morphe.youtube.misc.engagement.engagementPanelHookPatch
import io.github.nexalloy.morphe.youtube.misc.litho.filter.LithoFilter
import io.github.nexalloy.morphe.youtube.misc.litho.filter.addLithoFilter
import io.github.nexalloy.morphe.youtube.misc.playservice.VersionCheck
import io.github.nexalloy.morphe.youtube.misc.playservice.is_20_14_or_greater
import io.github.nexalloy.morphe.youtube.misc.settings.PreferenceScreen
import io.github.nexalloy.morphe.youtube.misc.verticalscroll.FixVerticalScroll

val HideAds = patch(
    name = "Hide ads",
    description = "Adds options to remove general ads.",
) {
    dependsOn(
        FixVerticalScroll,
        LithoFilter,
        VersionCheck,
        engagementPanelHookPatch
    )

    PreferenceScreen.ADS.addPreferences(
//        SwitchPreference("morphe_hide_end_screen_store_banner"),
        SwitchPreference("morphe_hide_fullscreen_ads"),
        SwitchPreference("morphe_hide_general_ads"),
        SwitchPreference("morphe_hide_merchandise_banners"),
        SwitchPreference("morphe_hide_paid_promotion_label"),
        SwitchPreference("morphe_hide_self_sponsor_ads"),
        SwitchPreference("morphe_hide_shopping_links"),
        SwitchPreference("morphe_hide_view_products_banner"),
        SwitchPreference("morphe_hide_web_search_results"),
        SwitchPreference("morphe_hide_youtube_premium_promotions"),
    )

    addLithoFilter(AdsFilter())
    addEngagementPanelIdHook(AdsFilter::hidePlayerPopupAds)

    // TODO: Hide end screen store banner

    // Hide fullscreen ad
    LithoDialogBuilderFingerprint.hookMethod {
        var dialogField = ::LithoDialogField.field
        after {
            val buffer = it.args[0] as ByteArray?
            val dialog = dialogField.get(it.thisObject)
            AdsFilter.closeFullscreenAd(dialog, buffer)
        }
    }

    // Hide get premium
    GetPremiumViewFingerprint.hookMethod {
        after {
            if (AdsFilter.hideGetPremiumView()) {
                val view = it.thisObject as View
                XposedHelpers.callMethod(view, "setMeasuredDimension", 0, 0)
            }
        }
    }

    // Hide player overlay view. This can be hidden with a regular litho filter
    // but an empty space remains.
    if (is_20_14_or_greater) {
        PlayerOverlayTimelyShelfFingerprint.hookMethod {
            before {
                if (AdsFilter.hideAds()) it.result = null
            }
        }
    }

    // Hide ad views
    val adAttributionId = ResourceUtils.getIdIdentifier("ad_attribution")

    XposedHelpers.findAndHookMethod(
        View::class.java.name,
        lpparam.classLoader,
        "findViewById",
        Int::class.java.name,
        object : XC_MethodHook() {
            override fun afterHookedMethod(param: MethodHookParam) {
                if (param.args[0] == adAttributionId) {
                    Logger.printDebug { "Hide Ad Attribution View" }
                    AdsFilter.hideAdAttributionView(param.result as View)
                }
            }
        })

    /**
     * TODO [AdsFilter.hideAds] OsNameHook
     */
}