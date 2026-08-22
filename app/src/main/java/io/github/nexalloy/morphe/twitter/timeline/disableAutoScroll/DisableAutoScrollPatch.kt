package io.github.nexalloy.morphe.twitter.timeline.disableAutoScroll

import io.github.nexalloy.hookMethod
import io.github.nexalloy.patch

val DisableAutoScroll = patch(
    name = "Disable auto timeline scroll on launch",
    description = "Prevents the timeline from automatically scrolling to the latest post on launch.",
) {
    val targetMethod = ::disableAutoScrollTargetMethodResolved.method.also { it.isAccessible = true }
    val returnType = targetMethod.returnType

    val validReturnTypes = setOf(
        Boolean::class.javaPrimitiveType,
        Int::class.javaPrimitiveType,
        Byte::class.javaPrimitiveType,
        Short::class.javaPrimitiveType,
        Char::class.javaPrimitiveType,
        Float::class.javaPrimitiveType,
    )
    if (returnType !in validReturnTypes) {
        throw Exception(
            "DisableAutoScroll: resolved method '$targetMethod' has return type '$returnType', " +
                "expected a 32-bit primitive - the generic string fingerprint almost certainly " +
                "matched the wrong method on this Twitter version. Refusing to install the " +
                "override to avoid breaking feed loading."
        )
    }

    val forcedValue: Any = if (returnType == Boolean::class.javaPrimitiveType) false else 0

    targetMethod.hookMethod {
        before { param -> param.result = forcedValue }
    }
}
