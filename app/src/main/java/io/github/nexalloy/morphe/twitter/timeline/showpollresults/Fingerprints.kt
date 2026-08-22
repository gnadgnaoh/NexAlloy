package io.github.nexalloy.morphe.twitter.timeline.showpollresults

import io.github.nexalloy.morphe.Fingerprint
import io.github.nexalloy.morphe.Opcode
import io.github.nexalloy.morphe.findFieldDirect
import io.github.nexalloy.morphe.string
import org.luckypray.dexkit.query.enums.StringMatchType

internal object JsonCardInstanceDataFingerprint : Fingerprint(
    name = "parseField",
    filters = listOf(string("binding_values")),
) {
    init {
        classMatcher {
            className(".JsonCardInstanceData\$\$JsonObjectMapper", StringMatchType.EndsWith)
        }
    }
}

internal val pollBindingValuesFieldResolved = findFieldDirect {
    val instructions = JsonCardInstanceDataFingerprint().instructions ?: emptyList()
    val moveResultIndex = instructions.indexOfFirst { it.opcode == Opcode.MOVE_RESULT_OBJECT.opCode }
        .let { if (it < 0) 0 else it }
    instructions.drop(moveResultIndex + 1)
        .firstOrNull { it.opcode == Opcode.IPUT_OBJECT.opCode }
        ?.fieldRef
        ?: throw Exception("poll binding_values field not found")
}
