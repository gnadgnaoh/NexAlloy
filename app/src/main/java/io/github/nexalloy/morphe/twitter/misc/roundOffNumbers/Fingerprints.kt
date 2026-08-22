package io.github.nexalloy.morphe.twitter.misc.roundOffNumbers

import io.github.nexalloy.morphe.Fingerprint
import io.github.nexalloy.morphe.ResourceType
import io.github.nexalloy.morphe.resourceLiteral

/**
 * Confirmed via DEX analysis to match
 * Lcom/twitter/util/l;->b(Landroid/content/res/Resources;D)Ljava/lang/String;
 * - the shared number-abbreviation helper used across the app
 * (e.g. like/repost/follower counts). Takes the raw, not-yet-rounded
 * double value and the 3 integer/string resource pairs used to build
 * the "1.2K" / "3.4M" / "5.6B" suffix.
 */
internal object RoundOffNumbersFingerprint : Fingerprint(
    returnType = "Ljava/lang/String;",
    parameters = listOf("Landroid/content/res/Resources;", "D"),
    filters = listOf(
        resourceLiteral(ResourceType.INTEGER, "abbr_number_divider_billions"),
        resourceLiteral(ResourceType.INTEGER, "abbr_number_divider_millions"),
        resourceLiteral(ResourceType.INTEGER, "abbr_number_divider_thousands"),
        resourceLiteral(ResourceType.STRING, "abbr_number_unit_billions"),
        resourceLiteral(ResourceType.STRING, "abbr_number_unit_millions"),
        resourceLiteral(ResourceType.STRING, "abbr_number_unit_thousands"),
    ),
)
