package io.github.nexalloy.morphe.twitter.misc.recommendedusers

import io.github.nexalloy.morphe.Fingerprint

/**
 * Confirmed via DEX analysis: the real target is
 * Lcom/twitter/model/json/people/JsonProfileRecommendationModuleResponse;->r()Ljava/lang/Object;
 * (the constructor returns V and doesn't match). The last IGET_OBJECT
 * instruction in r() reads field "d" (an ArrayList), which is used
 * immediately afterwards to build the returned result object.
 */
internal object HideRecommendedUsersFingerprint : Fingerprint(
    definingClass = "Lcom/twitter/model/json/people/JsonProfileRecommendationModuleResponse;",
    returnType = "Ljava/lang/Object;",
    parameters = emptyList(),
)

