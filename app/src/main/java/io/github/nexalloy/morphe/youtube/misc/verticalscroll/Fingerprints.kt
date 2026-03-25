package io.github.nexalloy.morphe.youtube.misc.verticalscroll

import io.github.nexalloy.morphe.AccessFlags
import io.github.nexalloy.morphe.Opcode
import io.github.nexalloy.morphe.fingerprint
import org.luckypray.dexkit.query.enums.StringMatchType

val canScrollVerticallyFingerprint = fingerprint {
    accessFlags(AccessFlags.PUBLIC, AccessFlags.FINAL)
    returns("Z")
    parameters()
    opcodes(
        Opcode.MOVE_RESULT,
        Opcode.RETURN,
        Opcode.INVOKE_VIRTUAL,
        Opcode.MOVE_RESULT
    )
    classMatcher { className(".SwipeRefreshLayout", StringMatchType.EndsWith) }
}