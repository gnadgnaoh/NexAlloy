package io.github.nexalloy.morphe.twitter.timeline.forceHD

import io.github.nexalloy.patch

val ForceHD = patch(
    name = "Enable force HD videos",
    description = "Videos will be played in the highest quality available.",
) {
    PlayerSupportFingerprint.hookMethod {
        before { param ->
            val currentList = param.args.getOrNull(0) as? List<*> ?: return@before
            param.args[0] = timelineVideos(currentList)
        }
    }
}

