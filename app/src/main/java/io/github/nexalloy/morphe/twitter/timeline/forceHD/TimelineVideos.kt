package io.github.nexalloy.morphe.twitter.timeline.forceHD

import io.github.nexalloy.morphe.twitter.entity.Video

internal fun timelineVideos(videoEntities: List<*>): List<*> {
    return try {
        var maxBitrate = 0
        var maxVideoObject: Any? = null

        for (videoObject in videoEntities) {
            if (videoObject == null) continue
            val video = Video(videoObject)
            if (video.getExtension() != "mp4") continue

            val bitrate = video.getBitrate() ?: continue
            if (bitrate < maxBitrate) continue
            maxBitrate = bitrate
            maxVideoObject = videoObject
        }

        if (maxVideoObject != null) listOf(maxVideoObject) else videoEntities
    } catch (e: Throwable) {
        videoEntities
    }
}
