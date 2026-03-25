package io.github.nexalloy.morphe.youtube.misc.settings

import io.github.nexalloy.morphe.findClassDirect
import io.github.nexalloy.morphe.findMethodDirect
import io.github.nexalloy.morphe.findMethodListDirect
import io.github.nexalloy.morphe.resourceMappings
import org.luckypray.dexkit.query.enums.StringMatchType
import java.lang.reflect.Modifier

val PreferenceInflater_inflate = findMethodDirect {
    findMethod {
        matcher {
            returnType = "androidx.preference.Preference"
            paramTypes(
                "org.xmlpull.v1.XmlPullParser",
                "androidx.preference.PreferenceGroup",
                "android.content.Context",
                "java.lang.Object[]",
                null,
                "java.lang.String[]"
            )
            usingEqStrings(": No start tag found!", ": ")
        }
    }.single()
}

val PreferenceFragmentCompatClass = findClassDirect {
    findClass {
        matcher {
            usingStrings(
                "Could not create RecyclerView",
                "Content has view with id attribute 'android.R.id.list_container' that is not a ViewGroup class",
                "androidx.preference.PreferenceFragmentCompat.PREFERENCE_ROOT"
            )
        }
    }.single()
}

val PreferenceFragmentCompat_addPreferencesFromResource = findMethodDirect {
    PreferenceFragmentCompatClass().let { preferenceFragmentCompat ->
        preferenceFragmentCompat.findMethod {
            matcher {
                returnType = "void"
                paramTypes("int")
            }
        }.singleOrNull() ?: preferenceFragmentCompat.findMethod {
            matcher {
                name = "addPreferencesFromResource"
            }
        }.single()
    }
}

val licenseActivityClass = findClassDirect {
    findClass {
        matcher {
            className(".LicenseActivity", StringMatchType.EndsWith)
        }
    }.single()
}

val licenseActivitySuperOnCreate = findMethodDirect {
    licenseActivityClass().superClass!!.findMethod { matcher { name = "onCreate" } }.single()
}

val licenseActivityOnCreateFingerprint = findMethodDirect {
    licenseActivityClass().findMethod { matcher { name = "onCreate" } }.single()
}

val licenseActivityNOTonCreate = findMethodListDirect {
    licenseActivityClass().methods.filter { it.name != "onCreate" && it.isMethod }
}

val appearanceStringId get() = resourceMappings["string", "app_theme_appearance_dark"]

val setThemeFingerprint = findMethodDirect {
    findMethod {
        matcher {
            modifiers = Modifier.PUBLIC or Modifier.FINAL
            paramCount = 0
            addUsingNumber(appearanceStringId)
        }
    }.single { it.returnTypeName != "void" }
}