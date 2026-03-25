package io.github.nexalloy.morphe.youtube.video.videoid

import app.morphe.extension.shared.Logger
import io.github.nexalloy.patch

/**
 * Hooks the new video id when the video changes.
 *
 * Supports all videos (regular videos and Shorts).
 */
val videoIdHooks: MutableList<(String) -> Unit> = mutableListOf()

val VideoId = patch(
    description = "Hooks to detect when the video id changes.",
) {
    ::videoIdFingerprint.hookMethod {
        val videoIdMethod = ::PlayerResponseModel_getVideoId.method
        before { param ->
            val videoId = videoIdMethod(param.args[0]) as String
            Logger.printDebug { "setCurrentVideoId: $videoId" }
            videoIdHooks.forEach { it(videoId) }
        }
    }
}