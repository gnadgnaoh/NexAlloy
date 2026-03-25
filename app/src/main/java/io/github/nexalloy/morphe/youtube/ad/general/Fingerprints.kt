package io.github.nexalloy.morphe.youtube.ad.general

import io.github.nexalloy.morphe.AccessFlags
import io.github.nexalloy.morphe.Fingerprint
import io.github.nexalloy.morphe.Opcode
import io.github.nexalloy.morphe.OpcodesFilter
import io.github.nexalloy.morphe.ResourceType
import io.github.nexalloy.morphe.findFieldDirect
import io.github.nexalloy.morphe.methodCall
import io.github.nexalloy.morphe.opcode
import io.github.nexalloy.morphe.resourceLiteral
import io.github.nexalloy.morphe.string

private val ADD_METHOD_CALL = methodCall(
    opcode = Opcode.INVOKE_VIRTUAL,
    name = "add",
    parameters = listOf("Ljava/lang/Object;"),
    returnType = "Z",
)

internal object FullScreenEngagementAdContainerFingerprint : Fingerprint(
    accessFlags = listOf(AccessFlags.PUBLIC, AccessFlags.FINAL),
    returnType = "V",
    parameters = listOf(),
    filters = listOf(
        resourceLiteral(ResourceType.ID, "fullscreen_engagement_ad_container"),
        opcode(Opcode.IGET_BOOLEAN),
        ADD_METHOD_CALL,
        ADD_METHOD_CALL,
        methodCall(
            opcode = Opcode.INVOKE_VIRTUAL,
            name = "size",
            parameters = listOf(),
            returnType = "I"
        )
    )
)

internal object GetPremiumViewFingerprint : Fingerprint(
    definingClass = "Lcom/google/android/apps/youtube/app/red/presenter/CompactYpcOfferModuleView;",
    name = "onMeasure",
    accessFlags = listOf(AccessFlags.PROTECTED, AccessFlags.FINAL),
    returnType = "V",
    parameters = listOf("I", "I"),
    filters = OpcodesFilter.opcodesToFilters(
        Opcode.ADD_INT_2ADDR,
        Opcode.ADD_INT_2ADDR,
        Opcode.INVOKE_VIRTUAL,
        Opcode.RETURN_VOID,
    )
)

internal object LithoDialogBuilderFingerprint : Fingerprint(
    accessFlags = listOf(AccessFlags.PUBLIC, AccessFlags.FINAL),
    returnType = "V",
    parameters = listOf("[B", "L"),
    filters = listOf(
        methodCall(
            opcode = Opcode.INVOKE_VIRTUAL,
            name = "show"
        ),
        resourceLiteral(ResourceType.STYLE, "SlidingDialogAnimation"),
    )
)

val LithoDialogField = findFieldDirect {
    LithoDialogBuilderFingerprint.let {
        val dialogClass =
            it.instructionMatches.first().instruction.methodRef!!.declaredClass!!.descriptor

        it().instructions.reversed().first { instruction ->
            instruction.opcode == Opcode.IPUT_OBJECT.ordinal &&
                    instruction.fieldRef!!.typeSign == dialogClass
        }.fieldRef!!
    }
}

internal object PlayerOverlayTimelyShelfFingerprint : Fingerprint(
    accessFlags = listOf(AccessFlags.PUBLIC, AccessFlags.FINAL),
    returnType = "V",
    parameters = listOf("Ljava/lang/Object;"),
    filters = listOf(
        string("player_overlay_timely_shelf"),
        string("innertube_cue_range"),
        string("Null id"),
        string("Null onExitActions")
    )
)
