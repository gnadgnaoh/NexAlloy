package io.github.nexalloy.morphe.youtube.misc.litho.filter

import io.github.nexalloy.morphe.AccessFlags
import io.github.nexalloy.morphe.Fingerprint
import io.github.nexalloy.morphe.Opcode
import io.github.nexalloy.morphe.OpcodesFilter
import io.github.nexalloy.morphe.findClassDirect
import io.github.nexalloy.morphe.findFieldDirect
import io.github.nexalloy.morphe.findMethodDirect
import io.github.nexalloy.morphe.fingerprint
import io.github.nexalloy.morphe.youtube.shared.conversionContextFingerprintToString
import org.luckypray.dexkit.result.FieldUsingType

//val componentContextParserFingerprint = fingerprint {
//    strings("Number of bits must be positive")
//}

object ComponentCreateFingerprint : Fingerprint(
    strings = listOf(
        "Element missing correct type extension",
        "Element missing type"
    )
)

object ProtobufBufferReferenceFingerprint : Fingerprint(
    accessFlags = listOf(AccessFlags.PUBLIC, AccessFlags.FINAL),
    returnType = "V",
    parameters = listOf("I", "Ljava/nio/ByteBuffer;"),
    filters = OpcodesFilter.opcodesToFilters(
        Opcode.IPUT,
        Opcode.INVOKE_VIRTUAL,
        Opcode.MOVE_RESULT,
        Opcode.SUB_INT_2ADDR,
    )
)

val emptyComponentFingerprint = fingerprint {
    accessFlags(AccessFlags.PRIVATE, AccessFlags.CONSTRUCTOR)
    parameters()
    strings("EmptyComponent")
}

val lithoThreadExecutorFingerprint = fingerprint {
    accessFlags(AccessFlags.PUBLIC, AccessFlags.CONSTRUCTOR)
    parameters("I", "I", "I")
    classMatcher {
        superClass {
            descriptor = "Ljava/util/concurrent/ThreadPoolExecutor;"
        }
    }
    literal { 1L }
}

//region rvxp
val conversionContextClass = findClassDirect {
    conversionContextFingerprintToString(this).declaredClass!!
}
val identifierFieldData = findFieldDirect {
    val stringFieldIndex =
        if (findMethod { matcher { usingStrings(", pathInternal=") } }.any()) 2 else 1
    conversionContextClass(this).methods.single {
        it.isConstructor && it.paramCount != 0
    }.usingFields.filter {
        it.usingType == FieldUsingType.Write && it.field.typeName == String::class.java.name
    }[stringFieldIndex].field
}

val pathBuilderFieldData = findFieldDirect {
    conversionContextClass(this).fields.single { it.typeSign == "Ljava/lang/StringBuilder;" }
}

val emptyComponentClass = findClassDirect {
    emptyComponentFingerprint().declaredClass!!
}

val featureFlagCheck = findMethodDirect {
    findMethod {
        matcher {
            returnType = "boolean"
            paramTypes("long", "boolean")
            addInvoke { paramTypes(null, "long", "boolean") }
        }
    }.single()
}
//endregion
