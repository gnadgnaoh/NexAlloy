package io.github.nexalloy.morphe.google

import io.github.nexalloy.morphe.google.discover.FilterDiscoverAds

/**
 * Patch list for com.google.android.googlequicksearchbox (Google Search / Discover feed).
 *
 * No ExtensionResourceHook / morphe-extension assets are needed here because
 * the Discover patches work entirely via reflection without injecting module
 * resources into AGSA.
 */
val GoogleDiscoverPatches = arrayOf(
    FilterDiscoverAds,
)
