package io.github.nexalloy.morphe.twitter.timeline.live

import io.github.nexalloy.morphe.Fingerprint
import io.github.nexalloy.morphe.Opcode
import io.github.nexalloy.morphe.findFieldDirect
import io.github.nexalloy.morphe.opcode

/**
 * Matches piko's fingerprint exactly: defining class + presence of an
 * IGET_OBJECT instruction (no access-flag/name assumption).
 */
internal object HideLiveThreadsFingerprint : Fingerprint(
    definingClass = "Lcom/twitter/fleets/api/json/JsonFleetsTimelineResponse;",
    filters = listOf(
        opcode(Opcode.IGET_OBJECT),
    ),
)

/**
 * Piko takes the FIRST IGET_OBJECT field in the matched method.
 */
internal val hideLiveThreadsFieldResolved = findFieldDirect {
    val instructions = HideLiveThreadsFingerprint().instructions ?: emptyList()
    instructions.first { it.opcode == Opcode.IGET_OBJECT.opCode }.fieldRef
        ?: throw Exception("hideLiveThreadsField not found")
}

