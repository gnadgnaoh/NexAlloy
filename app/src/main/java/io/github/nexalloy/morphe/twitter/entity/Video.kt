package io.github.nexalloy.morphe.twitter.entity

import io.github.nexalloy.getIntFieldOrNull
import io.github.nexalloy.getObjectFieldOrNullAs

class Video(private val obj: Any) {

    fun getBitrate(): Int? = obj.getIntFieldOrNull("a")

    fun getMediaUrl(): String? = obj.getObjectFieldOrNullAs<String>("b")

    fun getCodec(): String? = obj.getObjectFieldOrNullAs<String>("c")

    fun getThumbnail(): String? = obj.getObjectFieldOrNullAs<String>("d")

    fun getExtension(): String = when (getCodec()) {
        "video/mp4" -> "mp4"
        "video/webm" -> "webm"
        "application/x-mpegURL" -> "m3u8"
        else -> "unknown"
    }

    override fun toString(): String =
        "Video [getBitrate()=${getBitrate()}, getMediaUrl()=${getMediaUrl()}, getCodec()=${getCodec()}, " +
            "getThumbnail()=${getThumbnail()}, getExtension()=${getExtension()}]"
}
