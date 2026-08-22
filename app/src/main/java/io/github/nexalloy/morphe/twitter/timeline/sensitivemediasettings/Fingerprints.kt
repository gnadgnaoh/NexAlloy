package io.github.nexalloy.morphe.twitter.timeline.sensitivemediasettings

import io.github.nexalloy.morphe.Fingerprint

internal object SensitiveMediaWarningFingerprint : Fingerprint(
    definingClass = "Lcom/twitter/model/json/core/JsonSensitiveMediaWarning\$\$JsonObjectMapper;",
    name = "parse",
    returnType = "Ljava/lang/Object;",
)
