package io.github.nexalloy.morphe.twitter.timeline.enableVidAutoAdvance

import io.github.nexalloy.morphe.findMethodDirect
import io.github.nexalloy.morphe.twitter.featureFlag.featureFlagPatch.FeatureFlagFingerprint

/**
 * The auto-advance duration threshold is NOT read through a dedicated
 * method - it's read through the same shared config-reader class that
 * FeatureFlagFingerprint already locates (Lcom/twitter/util/config/z;),
 * via its int(String key, int default) overload. Confirmed via DEX
 * analysis: the call site is
 *   Lcom/twitter/util/config/z;->d(Ljava/lang/String;I)I
 * with key = "immersive_video_auto_advance_duration_threshold".
 */
internal val configIntMethodResolved = findMethodDirect {
    FeatureFlagFingerprint().declaredClass!!.methods.first {
        it.returnTypeName == "int" && it.paramTypeNames == listOf("java.lang.String", "int")
    }
}

