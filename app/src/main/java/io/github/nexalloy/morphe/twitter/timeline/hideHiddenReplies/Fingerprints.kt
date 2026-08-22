package io.github.nexalloy.morphe.twitter.timeline.hideHiddenReplies

import io.github.nexalloy.morphe.Fingerprint

/**
 * Confirmed via DEX analysis: the real target is
 * Lcom/twitter/model/json/timeline/urt/JsonTimelineTweet;->r()Ljava/lang/Object;
 * - the only no-arg method in this class returning Object (the
 * constructor returns V, and the other method "s" takes a parameter).
 * The last IGET_BOOLEAN instruction in that method reads field "n",
 * which is passed as one of two adjacent boolean constructor arguments
 * further down in the method.
 */
internal object HideHiddenRepliesFingerprint : Fingerprint(
    definingClass = "Lcom/twitter/model/json/timeline/urt/JsonTimelineTweet;",
    returnType = "Ljava/lang/Object;",
    parameters = emptyList(),
)

