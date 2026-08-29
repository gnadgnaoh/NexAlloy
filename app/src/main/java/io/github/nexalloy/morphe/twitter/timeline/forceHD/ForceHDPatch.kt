package io.github.nexalloy.morphe.twitter.timeline.forceHD

import io.github.nexalloy.patch
import java.lang.reflect.Field
import java.lang.reflect.Method

val ForceHD = patch(
    name = "Enable force HD videos",
    description = "Videos will be played in the highest quality available.",
) {
    val configClass: Class<*> = BuildMediaItemFingerprint.method.parameterTypes[1]

    val capFlowField: Field = configClass.declaredFields.single { field ->
        val type = field.type
        type.methods.any { it.name == "getValue" && it.parameterCount == 0 } &&
            type.declaredMethods.any { isValueSetter(it) }
    }.apply { isAccessible = true }

    val capSetter: Method = capFlowField.type.declaredMethods
        .single { isValueSetter(it) }
        .apply { isAccessible = true }

    BuildMediaItemFingerprint.hookMethod {
        before { param ->
            val config = param.args.getOrNull(1) ?: return@before
            val flow = capFlowField.get(config) ?: return@before
            capSetter.invoke(flow, Integer.MAX_VALUE)
        }
    }
}

private fun isValueSetter(method: Method): Boolean =
    method.parameterCount == 1 &&
        method.parameterTypes[0] == Any::class.java &&
        method.returnType == Void.TYPE
