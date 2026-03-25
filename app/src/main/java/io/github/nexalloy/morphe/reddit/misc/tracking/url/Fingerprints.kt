package io.github.nexalloy.morphe.reddit.misc.tracking.url

import io.github.nexalloy.morphe.findMethodDirect

val shareLinkFormatterFingerprint = findMethodDirect {
    findMethod {
        matcher {
            returnType = "java.lang.String"
            paramTypes("java.lang.String", null)
            usingEqStrings(
                "url",
                "getQueryParameterNames(...)",
                "getQueryParameters(...)",
                "toString(...)"
            )
        }
    }.single()
}