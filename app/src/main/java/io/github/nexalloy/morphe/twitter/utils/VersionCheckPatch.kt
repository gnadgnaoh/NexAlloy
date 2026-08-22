package io.github.nexalloy.morphe.twitter.utils

import io.github.nexalloy.patch
import kotlin.properties.Delegates

var is_11_82_or_greater: Boolean by Delegates.notNull()
    private set

var is_11_88_or_greater: Boolean by Delegates.notNull()
    private set

var is_11_92_or_greater: Boolean by Delegates.notNull()
    private set

var is_11_40_or_greater: Boolean by Delegates.notNull()
    private set

// For blocking redirecting to X Lite patch.
var is_11_98_or_greater: Boolean by Delegates.notNull()
    private set

val VersionCheck = patch(name = "<VersionCheck>") {
    val packageInfo = appContext.packageManager.getPackageInfo(appContext.packageName, 0)
    val versionCode = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
        packageInfo.longVersionCode.toInt()
    } else {
        @Suppress("DEPRECATION") packageInfo.versionCode
    }

    fun isEqualsOrGreaterThan(version: Int): Boolean = versionCode >= version

    // 11.82.0-beta.1 (311820101) does not have libpairipcore.so, but 11.82.0-release.0 (31182000) has libpairipcore.so.
    is_11_82_or_greater = versionCode == 311820000 || isEqualsOrGreaterThan(311830000)
    is_11_88_or_greater = isEqualsOrGreaterThan(311880000)
    is_11_92_or_greater = isEqualsOrGreaterThan(311920000)
    is_11_40_or_greater = isEqualsOrGreaterThan(311400000)
    is_11_98_or_greater = isEqualsOrGreaterThan(311980000)
}
