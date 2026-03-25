package io.github.nexalloy.morphe.youtube.layout.sponsorblock

import io.github.nexalloy.morphe.AccessFlags
import io.github.nexalloy.morphe.Fingerprint
import io.github.nexalloy.morphe.Opcode
import io.github.nexalloy.morphe.OpcodesFilter
import io.github.nexalloy.morphe.accessFlags
import io.github.nexalloy.morphe.findFieldDirect
import io.github.nexalloy.morphe.findMethodDirect
import io.github.nexalloy.morphe.fingerprint
import io.github.nexalloy.morphe.parameters
import io.github.nexalloy.morphe.resourceMappings
import io.github.nexalloy.morphe.returns
import io.github.nexalloy.morphe.youtube.shared.seekbarFingerprint

internal object AppendTimeFingerprint : Fingerprint(
    accessFlags = listOf(AccessFlags.PUBLIC, AccessFlags.FINAL),
    returnType = "V",
    parameters = listOf(
        "Ljava/lang/CharSequence;", "Ljava/lang/CharSequence;", "Ljava/lang/CharSequence;"
    ),
    filters = OpcodesFilter.opcodesToFilters(
        Opcode.INVOKE_VIRTUAL,
        Opcode.MOVE_RESULT_OBJECT,
        Opcode.IGET_OBJECT,
        Opcode.IGET_OBJECT,
        Opcode.CHECK_CAST,
        Opcode.INVOKE_VIRTUAL,
        Opcode.MOVE_RESULT_OBJECT,
        Opcode.INVOKE_STATIC,
        Opcode.MOVE_RESULT,
    ),
)

val SponsorBarRect = findFieldDirect {
    val clazz = seekbarFingerprint().declaredClass!!
    clazz.findMethod {
        matcher {
            addInvoke {
                name = "invalidate"
                paramTypes("android.graphics.Rect")
            }
        }
    }.single().usingFields.last { it.field.typeName == "android.graphics.Rect" }.field
}

val seekbarOnDrawFingerprint = findMethodDirect {
    seekbarFingerprint().declaredClass!!.findMethod {
        matcher {
            name = "onDraw"
        }
    }.single()
}

val inset_overlay_view_layout get() = resourceMappings["id", "inset_overlay_view_layout"]

val controlsOverlayFingerprint = findMethodDirect {
    findMethod {
        matcher {
            addUsingNumber(inset_overlay_view_layout)
            paramCount = 0
            returnType = "void"
        }
    }.single()
}

val AdProgressTextVisibility = fingerprint {
    definingClass("Lcom/google/android/libraries/youtube/ads/player/ui/AdProgressTextView;")
    name("setVisibility")
}

internal val adProgressTextViewVisibilityFingerprint = findMethodDirect {
    AdProgressTextVisibility().callers.findMethod {
        matcher {
            accessFlags(AccessFlags.PUBLIC, AccessFlags.FINAL)
            returns("V")
            parameters("Z")
        }
    }.single()
}
