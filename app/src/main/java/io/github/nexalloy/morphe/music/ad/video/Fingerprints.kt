package io.github.nexalloy.morphe.music.ad.video

import io.github.nexalloy.morphe.Opcode
import io.github.nexalloy.morphe.findMethodDirect
import io.github.nexalloy.morphe.fingerprint

val showVideoAdsParentFingerprint = fingerprint {
    opcodes(
        Opcode.MOVE_RESULT_OBJECT,
        Opcode.INVOKE_VIRTUAL,
        Opcode.IGET_OBJECT,
    )
    strings("maybeRegenerateCpnAndStatsClient called unexpectedly, but no error.")
}

val showVideoAds = findMethodDirect {
    showVideoAdsParentFingerprint().invokes.findMethod {
        matcher {
            paramTypes("boolean")
        }
    }.single()
}