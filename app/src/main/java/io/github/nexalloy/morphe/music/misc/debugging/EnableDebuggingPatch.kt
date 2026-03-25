package io.github.nexalloy.morphe.music.misc.debugging

import io.github.nexalloy.patch
import io.github.nexalloy.morphe.music.misc.settings.PreferenceScreen
import io.github.nexalloy.morphe.shared.misc.debugging.EnableDebugging

val EnableDebugging = patch(
    name = "Enable debugging",
    description = "Adds options for debugging and exporting ReVanced logs to the clipboard.",
) {
    EnableDebugging(
        preferenceScreen = PreferenceScreen.MISC
    )
}
