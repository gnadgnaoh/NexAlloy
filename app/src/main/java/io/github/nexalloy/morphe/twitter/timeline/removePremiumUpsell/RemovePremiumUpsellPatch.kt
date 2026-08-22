package io.github.nexalloy.morphe.twitter.timeline.removePremiumUpsell

import io.github.nexalloy.morphe.twitter.featureFlag.featureFlagPatch.FeatureFlagHook
import io.github.nexalloy.morphe.twitter.featureFlag.featureFlagPatch.featureFlagOverrides
import io.github.nexalloy.patch

val RemovePremiumUpsell = patch(
    name = "Remove premium upsell",
    description = "Removes premium upsells.",
) {
    dependsOn(FeatureFlagHook)

    val flags = listOf(
        "subscriptions_upsells_premium_home_nav",
        "subscriptions_enabled",
        "subscriptions_upsells_get_verified_profile",
        "subscriptions_upsells_get_verified_drawer_discount_enabled",
        "subscriptions_upsells_get_verified_profile_fatigue_enabled",
        "subscriptions_upsells_api_enabled",
        "subscriptions_upsells_user_profile_name_migration_enabled",
        "subscriptions_upsells_profile_card_enable",
        "subscriptions_upsells_get_verified_drawer_card_enabled",
        "subscriptions_upsells_get_verified_profile_discount_visitor_enabled",
        "subscriptions_upsells_analytics_profile_enabled",
        "subscriptions_upsells_articles_post_composer_promo_variant_enabled",
        "subscriptions_upsells_bookmark_folders_enabled",
        "subscriptions_upsells_verified_profile_visitor_upsell_enabled",
        "subscriptions_upsells_verified_profile_visitor_upsell_redesign_enabled",
        "subscriptions_upsells_get_verified_profile_card",
        "subscriptions_upsells_get_verified_profile_discount_own_enabled",
        "subscriptions_upsells_get_verified_profile_rotation_enabled",
        "subscriptions_upsells_home_nav_migration_enabled",
        "subscriptions_upsells_profile_card_enabled",
        "subscriptions_upsells_quick_display_settings",
        "subscriptions_upsells_track_interactions_enabled",
        "subscriptions_upsells_user_profile_header_migration_enabled",
    )

    for (flag in flags) {
        featureFlagOverrides[flag] = false
    }
}
