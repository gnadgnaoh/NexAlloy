package io.github.nexalloy.morphe.twitter.misc.fab

import io.github.nexalloy.morphe.Fingerprint

/**
 * Anchored on the "android_compose_fab_menu_enabled" string literal
 * instead of the class/return-type names, which are R8-minified and
 * NOT stable across Twitter updates - confirmed: between 12.11.1 and
 * 12.15.1 the return type alone changed from Lcom/twitter/ui/fab/u;
 * to Lcom/twitter/ui/fab/p; while the string and overall method shape
 * stayed identical. The string is unique across the whole APK, so no
 * other filter is needed.
 */
internal object FabProviderFingerprint : Fingerprint(
    strings = listOf("android_compose_fab_menu_enabled"),
)
