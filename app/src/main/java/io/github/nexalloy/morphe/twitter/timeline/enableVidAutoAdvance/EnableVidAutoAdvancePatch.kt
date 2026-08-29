package io.github.nexalloy.morphe.twitter.timeline.enableVidAutoAdvance

import io.github.nexalloy.patch
import java.lang.reflect.Proxy

private const val AUTO_ADVANCE_ENABLED = 2   // PersistentVideoSettings
private const val LOOP_STRATEGY = 4          // PlayerConfiguration

val EnableVidAutoAdvance = patch(
    name = "Control video auto scroll",
    description = "Disables auto-advancing to the next video in immersive video view (video replays instead).",
) {

    PersistentVideoSettingsConstructorFingerprint.hookMethod {
        before { param -> param.args[AUTO_ADVANCE_ENABLED] = false }
    }

    val loopStrategyInterface: Class<*> =
        PlayerConfigurationConstructorFingerprint.constructor.parameterTypes[LOOP_STRATEGY]

    val infiniteLoop = Proxy.newProxyInstance(
        loopStrategyInterface.classLoader ?: classLoader,
        arrayOf(loopStrategyInterface),
    ) { proxy, method, args ->
        when (method.name) {
            "equals" -> proxy === args?.getOrNull(0)
            "hashCode" -> System.identityHashCode(proxy)
            "toString" -> "NexAlloyInfiniteLoop"
            else -> if (method.returnType == Int::class.javaPrimitiveType) {
                Integer.MAX_VALUE
            } else {
                null
            }
        }
    }

    PlayerConfigurationConstructorFingerprint.hookMethod {
        before { param -> param.args[LOOP_STRATEGY] = infiniteLoop }
    }
}
