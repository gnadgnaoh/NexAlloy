package io.github.nexalloy.morphe.reddit.ad.general

import io.github.nexalloy.morphe.findMethodDirect
import io.github.nexalloy.morphe.fingerprint
import org.luckypray.dexkit.query.enums.StringMatchType

val adPostFingerprint = fingerprint {
    returns("V")
    // "children" are present throughout multiple versions
    strings("children")
    classMatcher { className(".Listing", StringMatchType.EndsWith) }
}
val AdPostSectionInitFingerprint = findMethodDirect {
    findClass {
        matcher {
            usingStrings("AdPostSection(linkId=")
        }
    }.single().methods.single { it.isConstructor }
}