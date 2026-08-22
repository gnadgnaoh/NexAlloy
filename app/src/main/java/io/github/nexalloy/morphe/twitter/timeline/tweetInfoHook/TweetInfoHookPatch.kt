package io.github.nexalloy.morphe.twitter.timeline.tweetInfoHook

import app.morphe.extension.shared.Logger
import io.github.nexalloy.patch
import io.github.nexalloy.setBooleanField
import io.github.nexalloy.setObjectField

internal var hidePromoteButtonEnabled = false
internal var hideCommunityNotesEnabled = false
internal var forceTranslateEnabled = false

private const val PROMOTE_ELIGIBILITY_CLASS =
    "com.twitter.model.json.core.JsonTweetQuickPromoteEligibility"

private var commNotesFieldName: String? = null
private var promoteButtonFieldName: String? = null
private var translateFieldName: String? = null

/**
 * Resolves and caches the 3 field names in a single pass over
 * getDeclaredFields(), mirroring piko's TweetInfo.loader(): the first
 * boolean field found is the community-notes flag, the second boolean
 * field found is the translate-availability flag, and the field whose
 * declared type is exactly JsonTweetQuickPromoteEligibility is the
 * promote-button field.
 */
private fun loadFieldNames(tweetClass: Class<*>) {
    val promoteClass = runCatching {
        Class.forName(PROMOTE_ELIGIBILITY_CLASS, false, tweetClass.classLoader)
    }.getOrNull()

    var commNotes: String? = null
    var translate: String? = null
    var promote: String? = null

    for (field in tweetClass.declaredFields) {
        if (field.type == Boolean::class.javaPrimitiveType) {
            if (commNotes == null) {
                commNotes = field.name
            } else if (translate == null) {
                translate = field.name
            }
            continue
        }
        if (promoteClass != null && field.type == promoteClass) {
            promote = field.name
        }
    }

    commNotesFieldName = commNotes
    translateFieldName = translate
    promoteButtonFieldName = promote
}

/**
 * Applies all enabled tweet-info tweaks to a single parsed JsonApiTweet
 * instance. Mirrors piko's TweetInfo.checkEntry().
 */
internal fun checkTweetInfoEntry(tweet: Any?) {
    if (tweet == null) return
    if (!hidePromoteButtonEnabled && !hideCommunityNotesEnabled && !forceTranslateEnabled) return

    runCatching {
        if (commNotesFieldName == null || promoteButtonFieldName == null || translateFieldName == null) {
            loadFieldNames(tweet.javaClass)
        }

        if (hideCommunityNotesEnabled) {
            commNotesFieldName?.let { tweet.setBooleanField(it, false) }
        }
        if (hidePromoteButtonEnabled) {
            promoteButtonFieldName?.let { tweet.setObjectField(it, null) }
        }
        if (forceTranslateEnabled) {
            translateFieldName?.let { tweet.setBooleanField(it, true) }
        }
    }.onFailure { e ->
        Logger.printException({ "[Twitter] TweetInfoHook: failed to apply tweet info tweaks" }, e)
    }
}

/** Always-on infrastructure patch (hidden from the per-app settings list, name starts with "<"). */
val TweetInfoHook = patch(name = "<TweetInfoHook>") {
    TweetInfoHookFingerprint.hookMethod {
        after { param -> checkTweetInfoEntry(param.result) }
    }
}

