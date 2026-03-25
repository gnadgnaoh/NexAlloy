package io.github.nexalloy.morphe.youtube.layout.buttons.navigation

import io.github.nexalloy.morphe.AccessFlags
import io.github.nexalloy.morphe.Fingerprint
import io.github.nexalloy.morphe.Opcode
import io.github.nexalloy.morphe.OpcodesFilter
import io.github.nexalloy.morphe.findMethodDirect
import io.github.nexalloy.morphe.fingerprint
import io.github.nexalloy.morphe.strings

internal const val ANDROID_AUTOMOTIVE_STRING = "Android Automotive"

val addCreateButtonViewFingerprint = fingerprint {
    strings("Android Wear", ANDROID_AUTOMOTIVE_STRING)
}

// rvxp
val AutoMotiveFeatureMethod = findMethodDirect {
    addCreateButtonViewFingerprint().invokes.findMethod {
        matcher { strings("android.hardware.type.automotive") }
    }.single()
}

internal object CreatePivotBarFingerprint : Fingerprint(
    accessFlags = listOf(AccessFlags.PUBLIC, AccessFlags.CONSTRUCTOR),
    returnType = "V",
    parameters = listOf(
        "Lcom/google/android/libraries/youtube/rendering/ui/pivotbar/PivotBar;",
        "Landroid/widget/TextView;",
        "Ljava/lang/CharSequence;",
    ),
    filters = OpcodesFilter.opcodesToFilters(
        Opcode.INVOKE_VIRTUAL,
        Opcode.RETURN_VOID,
    ),
)
