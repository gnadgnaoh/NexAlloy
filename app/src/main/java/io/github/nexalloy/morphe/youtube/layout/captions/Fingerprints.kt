package io.github.nexalloy.morphe.youtube.layout.captions

import io.github.nexalloy.morphe.AccessFlags
import io.github.nexalloy.morphe.Fingerprint
import io.github.nexalloy.morphe.Opcode
import io.github.nexalloy.morphe.OpcodesFilter
import io.github.nexalloy.RequireAppVersion
import io.github.nexalloy.morphe.literal

internal object StartVideoInformerFingerprint : Fingerprint(
    accessFlags = listOf(AccessFlags.PUBLIC, AccessFlags.FINAL),
    returnType = "V",
    filters = OpcodesFilter.opcodesToFilters(
        Opcode.INVOKE_INTERFACE,
        Opcode.RETURN_VOID,
    ),
    strings = listOf("pc"),
)

/**
 * YouTube 20.26+
 */
@RequireAppVersion("20.26.00")
internal object NoVolumeCaptionsFeatureFlagFingerprint : Fingerprint(
    accessFlags = listOf(AccessFlags.PUBLIC, AccessFlags.FINAL),
    returnType = "Z",
    filters = listOf(
        literal(45692436L)
    ),
)
