package io.github.nexalloy.morphe.music.audio.exclusiveaudio

import io.github.nexalloy.morphe.findMethodDirect
import java.lang.reflect.Modifier

val AllowExclusiveAudioPlaybackFingerprint = findMethodDirect {
    findMethod {
        matcher { addEqString("probably_has_unlimited_entitlement") }
    }.single().invokes.findMethod {
        matcher {
            returnType = "boolean"
            modifiers = Modifier.PUBLIC or Modifier.FINAL
            paramCount = 0
        }
    }.single()
}