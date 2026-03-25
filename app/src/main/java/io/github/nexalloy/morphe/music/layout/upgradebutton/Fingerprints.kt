package io.github.nexalloy.morphe.music.layout.upgradebutton

import io.github.nexalloy.morphe.AccessFlags
import io.github.nexalloy.morphe.Opcode
import io.github.nexalloy.morphe.findFieldDirect
import io.github.nexalloy.morphe.fingerprint

internal val pivotBarConstructorFingerprint = fingerprint {
    accessFlags(AccessFlags.PUBLIC, AccessFlags.CONSTRUCTOR)
    returns("V")
    parameters("L", "Z")
    opcodes(
        Opcode.INVOKE_INTERFACE,
        Opcode.GOTO,
        Opcode.IPUT_OBJECT,
        Opcode.RETURN_VOID
    )
}

val pivotBarElementField = findFieldDirect {
    pivotBarConstructorFingerprint().declaredClass!!.fields.single { f -> f.typeName == "java.util.List" }
}