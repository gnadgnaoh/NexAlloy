package io.github.nexalloy.morphe.twitter.featureFlag.featureFlagPatch

import io.github.nexalloy.patch

internal val featureFlagOverrides = mutableMapOf<String, Boolean>()

/** Always-on infrastructure patch (hidden from the per-app settings list, name starts with "<"). */
val FeatureFlagHook = patch(name = "<FeatureFlagHook>") {
    ::featureFlagBooleanMethodResolved.hookMethod {
        after { param ->
            val flagName = param.args.getOrNull(0) as? String ?: return@after
            featureFlagOverrides[flagName]?.let { override ->
                param.result = override
            }
        }
    }
}
