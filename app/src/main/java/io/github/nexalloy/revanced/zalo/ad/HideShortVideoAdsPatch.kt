package io.github.nexalloy.revanced.zalo.ads

import android.view.View
import app.morphe.extension.shared.Logger
import io.github.nexalloy.hookMethod
import io.github.nexalloy.patch

val HideShortVideoAds = patch(
    name = "Hide Zalo Video ads",
    description = "Hides ad containers, native ad layouts and outstream ads in Zalo Video.",
) {
    val resolved = buildList {
        add("outstream" to ::outstreamAdsLayoutFingerprint.dexMethod.className)
        add("adsTemplate" to ::adsTemplateLayoutFingerprint.dexMethod.className)
        add("adsNative" to ::adsNativeLayoutFingerprint.dexMethod.className)
        ::advertisingItemFingerprints.dexMethodList.forEach {
            add("advertisingItem" to it.className)
        }
    }.distinctBy { it.second }

    val failures = mutableListOf<String>()
    var hooked = 0

    for ((label, className) in resolved) {
        try {
            val cls = classLoader.loadClass(className)

            if (cls.isInterface) {
                Logger.printDebug { "[Zalo] $label -> $className (interface, skipped)" }
                continue
            }

            val constructors = cls.declaredConstructors
            check(constructors.isNotEmpty()) { "no declared constructor" }

            constructors.forEach { constructor ->
                constructor.isAccessible = true
                constructor.hookMethod {
                    after { param ->
                        (param.thisObject as? View)?.visibility = View.GONE
                    }
                }
            }

            hooked++
            Logger.printInfo { "[Zalo] $label -> $className (${constructors.size} ctor)" }
        } catch (t: Throwable) {
            failures += "$label -> $className (${t.javaClass.simpleName}: ${t.message})"
        }
    }

    if (failures.isNotEmpty()) {
        error("Zalo Video ad hooks failed: ${failures.joinToString("; ")}")
    }
    check(hooked > 0) { "Zalo Video: every resolved target was skipped" }
}
