package io.github.nexalloy.morphe.twitter.timeline.enableVidAutoAdvance

import io.github.nexalloy.patch

private const val AUTO_ADVANCE_THRESHOLD_KEY = "immersive_video_auto_advance_duration_threshold"

val EnableVidAutoAdvance = patch(
    name = "Control video auto scroll",
    description = "Disables auto-advancing to the next video in immersive video view (video replays instead).",
) {
    ::configIntMethodResolved.hookMethod {
        after { param ->
            val key = param.args.getOrNull(0) as? String ?: return@after
            if (key == AUTO_ADVANCE_THRESHOLD_KEY) {
                param.result = -1
            }
        }
    }
}

