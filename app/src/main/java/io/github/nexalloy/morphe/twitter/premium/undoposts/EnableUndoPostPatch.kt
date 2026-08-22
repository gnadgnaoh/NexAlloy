package io.github.nexalloy.morphe.twitter.premium.undoposts

import io.github.nexalloy.morphe.twitter.featureFlag.featureFlagPatch.FeatureFlagHook
import io.github.nexalloy.morphe.twitter.featureFlag.featureFlagPatch.featureFlagOverrides
import io.github.nexalloy.patch

val EnableUndoPosts = patch(
    name = "Enable Undo Posts",
    description = "Enables the ability to undo a post for a few seconds before it's actually posted.",
) {
    dependsOn(FeatureFlagHook)

    val flags = listOf(
        "subscriptions_feature_1003",
        "allow_undo_replies",
        "allow_undo_tweet",
    )

    for (flag in flags) {
        featureFlagOverrides[flag] = true
    }
}
