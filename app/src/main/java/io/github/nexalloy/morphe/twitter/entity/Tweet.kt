package io.github.nexalloy.morphe.twitter.entity

import app.morphe.extension.shared.Logger


class Tweet(private val obj: Any) {

    fun getTweetUsername(): String? = tweetUsernameMethod.invoke(obj) as? String

    fun getTweetProfileName(): String? = tweetProfileNameMethod.invoke(obj) as? String

    fun getTweetUserId(): Long? = tweetUserIdMethod.invoke(obj) as? Long

    fun getMedias(): List<Media> {
        val mediaData = mutableListOf<Media>()

        val mediaRootObject = tweetMediaMethod.invoke(obj) ?: return mediaData

        val list = extMediaListField.get(mediaRootObject) as? List<*> ?: return mediaData
        if (list.isEmpty()) return mediaData

        for (item in list) {
            if (item == null) continue
            val media = ExtMediaEntities(item).getMedia()
            mediaData.add(media)
        }
        return mediaData
    }

    fun getTweetInfo(): TweetInfo? {
        val data = tweetInfoField.get(obj) ?: return null
        return TweetInfo(data)
    }

    fun getTweetLang(): String? = getTweetInfo()?.getLang()

    fun getLongText(): String? {
        val noteTweetObj = tweetNoteTweetMethod.invoke(obj) ?: return null
        return longTextField.get(noteTweetObj) as? String
    }

    fun getShortText(): String? = tweetShortTextMethod.invoke(obj) as? String

    fun getText(): String = try {
        var text = getLongText() ?: getShortText().orEmpty()
        text = text.replace(Regex("""pic\.x\.com/\S+"""), "")
        text = text.replace(Regex("""https?://t\.co/\S+"""), "")
        text
    } catch (e: Throwable) {
        Logger.printException { "Tweet.getText failed" }
        e.message.orEmpty()
    }

    override fun toString(): String = try {
        "Tweet [getTweetUsername()=${getTweetUsername()}, getTweetProfileName()=${getTweetProfileName()}, " +
            "getTweetUserId()=${getTweetUserId()}, getMedias()=${getMedias()}, getTweetInfo()=${getTweetInfo()}, " +
            "getTweetLang()=${getTweetLang()}, getLongText()=${getLongText()}, getShortText()=${getShortText()}]"
    } catch (e: Throwable) {
        Logger.printException { "Tweet.toString failed" }
        e.message.orEmpty()
    }
}
