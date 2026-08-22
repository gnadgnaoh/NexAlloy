package io.github.nexalloy.morphe.twitter.featureFlag.featureFlagPatch

import io.github.nexalloy.morphe.Fingerprint
import io.github.nexalloy.morphe.findMethodDirect
import io.github.nexalloy.morphe.string

internal object FeatureFlagFingerprint : Fingerprint(
    filters = listOf(
        string("feature_switches_configs_crashlytics_enabled"),
    ),
)

internal val featureFlagBooleanMethodResolved = findMethodDirect {
    FeatureFlagFingerprint().declaredClass!!.methods.first {
        it.returnTypeName == "boolean" && it.paramTypeNames == listOf("java.lang.String", "boolean")
    }
}
