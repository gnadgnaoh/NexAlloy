package io.github.nexalloy.morphe.twitter.misc.roundOffNumbers

import app.morphe.extension.shared.Logger
import io.github.nexalloy.patch
import java.math.RoundingMode
import java.text.NumberFormat

/**
 * Mirrors Lcom/twitter/util/l;->c()Ljava/text/NumberFormat;'s fallback
 * formatter: NumberFormat.getNumberInstance() with RoundingMode.DOWN, 0
 * max fraction digits, and grouping enabled. We use the default locale
 * since the app's cached-locale helper isn't accessible from here.
 */
private val fallbackFormat: NumberFormat by lazy {
    NumberFormat.getNumberInstance().apply {
        roundingMode = RoundingMode.DOWN
        maximumFractionDigits = 0
        isGroupingUsed = true
    }
}

val RoundOffNumbers = patch(
    name = "Round off numbers",
    description = "Disables abbreviating large numbers (e.g. \"1.2K\") in favor of the full number.",
) {
    RoundOffNumbersFingerprint.hookMethod {
        after { param ->
            runCatching {
                val value = param.args[1] as? Double ?: return@after
                param.result = fallbackFormat.format(value)
            }.onFailure { e ->
                Logger.printException({ "[Twitter] RoundOffNumbers: failed to format number" }, e)
            }
        }
    }
}
