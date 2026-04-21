package io.github.nexalloy.morphe.shared.ad

import io.github.nexalloy.morphe.AccessFlags
import io.github.nexalloy.morphe.Fingerprint
import io.github.nexalloy.morphe.Opcode
import io.github.nexalloy.morphe.ResourceType
import io.github.nexalloy.morphe.findFieldDirect
import io.github.nexalloy.morphe.methodCall
import io.github.nexalloy.morphe.resourceLiteral


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