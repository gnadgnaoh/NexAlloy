package io.github.nexalloy.morphe.twitter.misc.searchsuggestions

import io.github.nexalloy.morphe.Fingerprint
import org.luckypray.dexkit.query.enums.StringMatchType

internal object SearchDbInsertFingerprint : Fingerprint(
    strings = listOf(
        "search_queries",
        "findSearchQuery: ",
        "LOWER(query)=LOWER(?) AND LOWER(name)=LOWER(?) AND type=? AND latitude=? AND longitude=?",
    ),
)

/**
 * Confirmed via DEX analysis: Lcom/twitter/search/provider/m;->b(...)
 * is the real target - reads suggestions from a Cursor. Piko short-circuits
 * at the very top of the method (returns null immediately), so a before
 * hook is safe here (no field is read/used before our override applies).
 */
internal object SearchSuggestionFingerprint : Fingerprint(
    returnType = "Ljava/util/Collection;",
    strings = listOf(
        "type",
        "query_id",
    ),
) {
    init {
        classMatcher {
            className("search.provider", StringMatchType.Contains)
        }
    }
}

