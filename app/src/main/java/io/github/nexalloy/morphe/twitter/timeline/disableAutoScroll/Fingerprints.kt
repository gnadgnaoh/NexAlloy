package io.github.nexalloy.morphe.twitter.timeline.disableAutoScroll

import io.github.nexalloy.morphe.Fingerprint
import io.github.nexalloy.morphe.findMethodDirect

internal object DisableAutoScrollFingerprint : Fingerprint(
    returnType = "V",
    strings = listOf(
        "applicationManager",
        "releaseCompletable",
        "preferences",
        "twSystemClock",
        "launchTracker",
        "cold_start_launch_time_millis",
    ),
)

internal val disableAutoScrollTargetMethodResolved = findMethodDirect {
    DisableAutoScrollFingerprint().declaredClass!!.methods.last()
}
