package io.github.nexalloy.morphe.twitter.timeline.sensitivemediasettings

import io.github.nexalloy.setBooleanField
import io.github.nexalloy.patch

val ShowSensitiveMedia = patch(
    name = "Show sensitive media",
    description = "Disables the sensitive media warning/blur on posts.",
) {
    SensitiveMediaWarningFingerprint.hookMethod {
        after { param ->
            val result = param.result ?: return@after
            runCatching {
                result.setBooleanField("a", false)
                result.setBooleanField("b", false)
                result.setBooleanField("c", false)
            }
        }
    }
}
