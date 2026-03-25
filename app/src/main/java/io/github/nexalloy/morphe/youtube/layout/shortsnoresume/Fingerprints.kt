package io.github.nexalloy.morphe.youtube.layout.shortsnoresume

import io.github.nexalloy.morphe.AccessFlags
import io.github.nexalloy.morphe.Fingerprint
import io.github.nexalloy.morphe.Opcode
import io.github.nexalloy.morphe.accessFlags
import io.github.nexalloy.morphe.findMethodDirect
import io.github.nexalloy.morphe.fingerprint
import io.github.nexalloy.morphe.literal
import io.github.nexalloy.morphe.opcodes
import io.github.nexalloy.morphe.parameters
import io.github.nexalloy.morphe.returns

val userWasInShortsFingerprint = findMethodDirect {
    runCatching {
        fingerprint {
            returns("V")
            accessFlags(AccessFlags.PUBLIC, AccessFlags.FINAL)
            parameters("Ljava/lang/Object;")
            strings("userIsInShorts: ")
        }
    }.getOrElse {
        findMethod {
            matcher {
                returns("V")
                accessFlags(AccessFlags.PUBLIC, AccessFlags.FINAL)
                parameters("Ljava/lang/Object;")
                opcodes(
                    Opcode.INVOKE_INTERFACE, // userWasInShortsProtoStoreProvider
                    Opcode.MOVE_RESULT_OBJECT,
                    Opcode.CHECK_CAST,
                    Opcode.NEW_INSTANCE,
                    Opcode.INVOKE_DIRECT, // userWasInShortsBuilder
                    Opcode.INVOKE_INTERFACE,
                    Opcode.RETURN_VOID,
                )
            }
        }.findMethod {
            matcher {
                opcodes(
                    Opcode.CHECK_CAST, // p1, Ljava/lang/Boolean; // userIsInShorts
                    Opcode.INVOKE_VIRTUAL, // Ljava/lang/Boolean;->booleanValue()Z
                    Opcode.MOVE_RESULT,
                    Opcode.IGET_OBJECT,
                    Opcode.MOVE_OBJECT,
                    Opcode.CHECK_CAST,
                    Opcode.IGET_OBJECT,
                    Opcode.INVOKE_INTERFACE, // userWasInShortsProtoStoreProvider
                )
            }
        }.single()
    }
}

val userWasInShortsBuilderFingerprint = findMethodDirect {
    val paramTypes = listOf("boolean", "int")
    userWasInShortsFingerprint().invokes.single {
        it.paramTypeNames.takeLast(2) == paramTypes
    }
}

/**
 * 18.15.40+
 */
internal object UserWasInShortsConfigFingerprint : Fingerprint(
    accessFlags = listOf(AccessFlags.PUBLIC, AccessFlags.FINAL),
    returnType = "Z",
    parameters = listOf(),
    filters = listOf(
        literal(45358360L)
    ),
)
