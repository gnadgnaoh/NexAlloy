package io.github.nexalloy.morphe.twitter

import io.github.nexalloy.Patch
import io.github.nexalloy.morphe.twitter.ads.timelineEntryHook.HideAds
import io.github.nexalloy.morphe.twitter.ads.timelineEntryHook.HideRecommendationItems
import io.github.nexalloy.morphe.twitter.link.unshorten.NoShortenedUrl
import io.github.nexalloy.morphe.twitter.premium.enableForcePip.EnableForcePip
import io.github.nexalloy.morphe.twitter.timeline.enableVidAutoAdvance.EnableVidAutoAdvance
import io.github.nexalloy.morphe.twitter.timeline.forceHD.ForceHD
import io.github.nexalloy.morphe.twitter.timeline.forceTranslate.ForceTranslate
import io.github.nexalloy.morphe.twitter.timeline.removePremiumUpsell.RemovePremiumUpsell
import io.github.nexalloy.morphe.twitter.timeline.sensitivemediasettings.ShowSensitiveMedia
import io.github.nexalloy.morphe.twitter.timeline.showpollresults.ShowPollResults

val TwitterPatches: Array<Patch> = arrayOf(
    // Ads / recommendations
    HideAds,
    HideRecommendationItems,

    // Links
    NoShortenedUrl,

    // Premium
    EnableForcePip,
    RemovePremiumUpsell,

    // Timeline / video
    EnableVidAutoAdvance,
    ForceHD,
    ForceTranslate,
    ShowSensitiveMedia,
    ShowPollResults,
)
