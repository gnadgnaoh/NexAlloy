package io.github.nexalloy.morphe.youtube.ad.video

import app.morphe.extension.youtube.patches.VideoAdsPatch
import de.robv.android.xposed.XC_MethodReplacement
import io.github.nexalloy.patch

val VideoAds = patch(
    name = "Video ads",
    description = "Adds an option to remove ads in the video player.",
) {
    LoadVideoAdsFingerprint.hookMethod(XC_MethodReplacement.DO_NOTHING)
    PlayerBytesAdLayoutFingerprint.hookMethod(XC_MethodReplacement.DO_NOTHING)

    /**
     * TODO [VideoAdsPatch.hideVideoAds] OsNameHook
     */
}