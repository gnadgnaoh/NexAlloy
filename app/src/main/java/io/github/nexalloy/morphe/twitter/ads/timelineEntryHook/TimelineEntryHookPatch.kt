package io.github.nexalloy.morphe.twitter.ads.timelineEntryHook

import io.github.nexalloy.getObjectFieldOrNullAs
import io.github.nexalloy.patch

internal var hideAdsEnabled = false
internal var hideRevisitPinnedPostsEnabled = false
internal var hideCommunitiesToJoinEnabled = false
internal var hideCreatorsToSubscribeEnabled = false
internal var hideDetailedPostsEnabled = false
internal var hidePremiumPromptEnabled = false
internal var hideRevisitBookmarksEnabled = false
internal var hideTodaysNewsEnabled = false
internal var hideTopPeopleSearchEnabled = false
internal var hideWhoToFollowEnabled = false

internal fun isEntryIdRemove(entryId: String?): Boolean {
    if (entryId == null) return false
    val split = entryId.split("-")
    val entryId2 = split.getOrElse(0) { "" }

    if (entryId2 == "cursor" || entryId2 == "Guide" || entryId2.startsWith("semantic_core")) {
        return false
    }

    return when {
        (entryId.contains("promoted") || (entryId2 == "conversationthread" && split.size == 3)) && hideAdsEnabled -> true
        (entryId2 == "superhero" || entryId2 == "eventsummary") && hideAdsEnabled -> true
        entryId.contains("rtb") && hideAdsEnabled -> true
        entryId2.startsWith("tweetdetail") && hideDetailedPostsEnabled -> true
        entryId2 == "bookmarked" && hideRevisitBookmarksEnabled -> true
        entryId.startsWith("community-to-join") && hideCommunitiesToJoinEnabled -> true
        entryId.startsWith("who-to-follow") && hideWhoToFollowEnabled -> true
        entryId.startsWith("who-to-subscribe") && hideCreatorsToSubscribeEnabled -> true
        entryId.startsWith("pinned-tweets") && hideRevisitPinnedPostsEnabled -> true
        entryId.startsWith("messageprompt-") && hidePremiumPromptEnabled -> true
        (entryId.startsWith("main-event-") || entryId2 == "pivot") && hideAdsEnabled -> true
        entryId2 == "toptabsrpusermodule" && hideTopPeopleSearchEnabled -> true
        entryId.startsWith("stories") && hideTodaysNewsEnabled -> true
        else -> false
    }
}

/** Always-on infrastructure patch (hidden from the per-app settings list). */
val TimelineEntryHook = patch(name = "<TimelineEntryHook>") {
    TimelineEntryHookFingerprint.hookMethod {
        after { param ->
            val entry = param.result ?: return@after
            val entryId = entry.getObjectFieldOrNullAs<String>("a")
            if (isEntryIdRemove(entryId)) param.result = null
        }
    }

    TimelineModuleItemHookFingerprint.hookMethod {
        after { param ->
            val item = param.result ?: return@after
            val entryId = item.getObjectFieldOrNullAs<String>("a")
            if (isEntryIdRemove(entryId)) param.result = null
        }
    }

    HidePromotedTrendFingerprint.hookMethod {
        after { param ->
            if (hideAdsEnabled) param.result = null
        }
    }
}
