package io.github.nexalloy.morphe.twitter.entity

import app.morphe.extension.shared.Logger

class ExtMediaEntities(private val obj: Any) {

    fun getImageUrl(): String? = extMediaImageField.get(obj) as? String

    fun getHighResImageUrl(): String? = getImageUrl()?.let { "$it?name=4096x4096&format=jpg" }

    fun getHighResVideo(): Video? {
        val videoInfoObject = extMediaVideoInfoField.get(obj) ?: return null
        val videoVariantObject = extMediaVideoVariantsField.get(videoInfoObject) ?: return null
        val videoVariants = videoVariantObject as? List<*> ?: return null

        val maxBitrate = 0
        var maxBitrateVideo: Video? = null
        for (videoObject in videoVariants) {
            if (videoObject == null) continue
            val video = Video(videoObject)
            val bitrate = video.getBitrate() ?: continue
            if (bitrate >= maxBitrate) {
                maxBitrateVideo = video
            }
        }
        return maxBitrateVideo
    }

    fun getMedia(): Media {
        val video = getHighResVideo()
        return if (video != null) {
            Media(Media.TYPE_VIDEO, video.getMediaUrl().orEmpty(), video.getExtension())
        } else {
            Media(Media.TYPE_IMAGE, getHighResImageUrl().orEmpty(), "jpg")
        }
    }

    override fun toString(): String = try {
        "ExtMediaEntities [getImageUrl()=${getImageUrl()}, getHighResImageUrl()=${getHighResImageUrl()}, " +
            "getHighResVideo()=${getHighResVideo()}]"
    } catch (e: Throwable) {
        Logger.printException { "ExtMediaEntities.toString failed" }
        e.message ?: "error"
    }
}
