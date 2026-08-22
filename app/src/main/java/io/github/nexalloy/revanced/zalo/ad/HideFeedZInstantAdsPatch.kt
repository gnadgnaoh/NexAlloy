package io.github.nexalloy.revanced.zalo.ads

import android.view.View
import android.view.ViewGroup
import io.github.nexalloy.hookMethod
import io.github.nexalloy.patch

val HideFeedZInstantAds = patch(
    name = "Hide feed ads",
    description = "Collapses sponsored zinstant items in the Nhật ký feed to zero height.",
) {
    ::feedAdsLayoutHeightFingerprint.hookMethod {
        before { param -> param.result = 0 }
    }

    val className = ::feedAdsBindFingerprint.dexMethod.className
    classLoader.loadClass(className)
        .getDeclaredMethod("onAttachedToWindow")
        .hookMethod {
            after { param ->
                val view = param.thisObject as? View ?: return@after
                view.visibility = View.GONE
                view.layoutParams?.let { lp ->
                    if (lp.height != 0) {
                        lp.height = 0
                        if (lp is ViewGroup.MarginLayoutParams) {
                            lp.topMargin = 0
                            lp.bottomMargin = 0
                        }
                        view.layoutParams = lp
                    }
                }
            }
        }
}
