package io.github.nexalloy.morphe.youtube.interaction.downloads

import io.github.nexalloy.morphe.AccessFlags
import io.github.nexalloy.morphe.Fingerprint
import io.github.nexalloy.morphe.string

internal object OfflineVideoEndpointFingerprint : Fingerprint(
    accessFlags = listOf(AccessFlags.PUBLIC, AccessFlags.FINAL),
    returnType = "V",
    parameters = listOf(
        "Ljava/util/Map;",
        "L",
        "Ljava/lang/String", // VideoId
        "L",
    ),
    filters = listOf(
        string("Object is not an offlineable video: ")
    )
)
