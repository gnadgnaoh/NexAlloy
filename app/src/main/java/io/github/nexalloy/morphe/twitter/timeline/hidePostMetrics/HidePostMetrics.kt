package io.github.nexalloy.morphe.twitter.timeline.hidePostMetrics

import io.github.nexalloy.patch

/**
 * Confirmed via DEX analysis: the target is the static method
 * Lcom/twitter/ui/tweet/d;->a(TweetStatView, String count, String
 * description). Piko blanks the SECOND parameter (the count value, e.g.
 * "42") right after its null-check passes - so we override args[1]
 * (not the last parameter) before the method runs.
 */
val HidePostMetrics = patch(
    name = "Hide post metrics",
    description = "Hides like, repost, etc. counts.",
) {
    InlineActionViewTextFingerprint.hookMethod {
        before { param -> param.result = Unit }
    }

    TweetStatViewTextFingerprint.hookMethod {
        before { param ->
            if (param.args.getOrNull(1) is String) {
                param.args[1] = ""
            }
        }
    }
}
