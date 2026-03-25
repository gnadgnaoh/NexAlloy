package io.github.nexalloy.morphe.youtube.misc.imageurlhook

import io.github.nexalloy.morphe.AccessFlags
import io.github.nexalloy.morphe.Fingerprint
import io.github.nexalloy.morphe.StringComparisonType
import io.github.nexalloy.morphe.findFieldDirect
import io.github.nexalloy.morphe.string
import org.luckypray.dexkit.result.FieldUsingType

private object OnResponseStartedFingerprint : Fingerprint(
    name = "onResponseStarted",
    accessFlags = listOf(AccessFlags.PUBLIC, AccessFlags.FINAL),
    returnType = "V",
    parameters = listOf("Lorg/chromium/net/UrlRequest;", "Lorg/chromium/net/UrlResponseInfo;"),
    strings = listOf(
        "Content-Length",
        "Content-Type",
        "identity",
        "application/x-protobuf",
    )
)
{
    init {
        classMatcher { superClass { descriptor("Lorg/chromium/net/UrlRequest\$Callback;") } }
    }
}

internal object OnFailureFingerprint : Fingerprint(
    classFingerprint = OnResponseStartedFingerprint,
    name = "onFailed",
    accessFlags = listOf(AccessFlags.PUBLIC, AccessFlags.FINAL),
    returnType = "V",
    parameters = listOf(
        "Lorg/chromium/net/UrlRequest;",
        "Lorg/chromium/net/UrlResponseInfo;",
        "Lorg/chromium/net/CronetException;"
    )
)

internal object OnSucceededFingerprint : Fingerprint(
    classFingerprint = OnResponseStartedFingerprint,
    name = "onSucceeded",
    accessFlags = listOf(AccessFlags.PUBLIC, AccessFlags.FINAL),
    returnType = "V",
    parameters = listOf("Lorg/chromium/net/UrlRequest;", "Lorg/chromium/net/UrlResponseInfo;")
)

internal const val CRONET_URL_REQUEST_CLASS_DESCRIPTOR = "Lorg/chromium/net/impl/CronetUrlRequest;"

internal object RequestFingerprint : Fingerprint(
    definingClass = CRONET_URL_REQUEST_CLASS_DESCRIPTOR,
    accessFlags = listOf(AccessFlags.PUBLIC, AccessFlags.CONSTRUCTOR),
)

val urlField = findFieldDirect {
    RequestFingerprint.run().usingFields.first {
        it.usingType == FieldUsingType.Write &&
                it.field.typeSign == "Ljava/lang/String;"
    }.field
}

private object MessageDigestImageUrlParentFingerprint : Fingerprint(
    accessFlags = listOf(AccessFlags.PUBLIC, AccessFlags.FINAL),
    returnType = "Ljava/lang/String;",
    parameters = listOf(),
    filters = listOf(
        string("@#&=*+-_.,:!?()/~'%;\$", StringComparisonType.STARTS_WITH),
    )
)

internal object MessageDigestImageUrlFingerprint : Fingerprint(
    classFingerprint = MessageDigestImageUrlParentFingerprint,
    accessFlags = listOf(AccessFlags.PUBLIC, AccessFlags.CONSTRUCTOR),
    parameters = listOf("Ljava/lang/String;", "L")
)
