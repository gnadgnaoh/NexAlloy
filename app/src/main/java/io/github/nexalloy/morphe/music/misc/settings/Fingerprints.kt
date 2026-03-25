package io.github.nexalloy.morphe.music.misc.settings

import io.github.nexalloy.morphe.findClassDirect
import io.github.nexalloy.morphe.findMethodDirect
import io.github.nexalloy.morphe.findMethodListDirect
import io.github.nexalloy.morphe.youtube.misc.settings.PreferenceFragmentCompatClass
import org.luckypray.dexkit.query.enums.StringMatchType

val PreferenceFragmentCompat_setPreferencesFromResource = findMethodDirect {
    PreferenceFragmentCompatClass().let { preferenceFragmentCompat ->
        preferenceFragmentCompat.findMethod {
            matcher {
                returnType = "void"
                paramTypes("int", "String")
            }
        }.singleOrNull() ?: preferenceFragmentCompat.findMethod {
            matcher {
                name = "setPreferencesFromResource"
            }
        }.single()
    }
}

val googleApiActivityClass = findClassDirect {
    findClass {
        matcher {
            className(".GoogleApiActivity", StringMatchType.EndsWith)
        }
    }.single()
}

internal val googleApiActivityFingerprint = findMethodDirect {
    googleApiActivityClass().findMethod { matcher { name = "onCreate" } }.single()
}

val googleApiActivityNOTonCreate = findMethodListDirect {
    googleApiActivityClass().methods.filter { it.name != "onCreate" && it.isMethod }
}