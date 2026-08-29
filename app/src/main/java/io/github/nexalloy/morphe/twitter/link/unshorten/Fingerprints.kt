package io.github.nexalloy.morphe.twitter.link.unshorten

import io.github.nexalloy.morphe.Fingerprint

internal object UrlEntityToStringFingerprint : Fingerprint(
    name = "toString",
    strings = listOf("UrlEntity(displayUrl="),
)

internal object UrlEntityConstructorFingerprint : Fingerprint(
    classFingerprint = UrlEntityToStringFingerprint,
    name = "<init>",
    custom = { paramCount = 5 },
)

internal object UrlEntitySerialConstructorFingerprint : Fingerprint(
    classFingerprint = UrlEntityToStringFingerprint,
    name = "<init>",
    custom = { paramCount = 6 },
)
