package io.github.nexalloy.morphe.twitter.entity

class TweetInfo(private val obj: Any) {

    fun getLang(): String? = tweetLangField.get(obj) as? String

    override fun toString(): String = "TweetInfo [getLang()=${getLang()}]"
}
