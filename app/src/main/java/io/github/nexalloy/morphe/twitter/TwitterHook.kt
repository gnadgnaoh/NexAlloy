package io.github.nexalloy.morphe.twitter

import io.github.nexalloy.Patch
import io.github.nexalloy.morphe.twitter.ads.timelineEntryHook.HideAds
import io.github.nexalloy.morphe.twitter.ads.timelineEntryHook.HideRecommendationItems
import io.github.nexalloy.morphe.twitter.featureFlag.DisableChirpFont
import io.github.nexalloy.morphe.twitter.link.cleartrackingparams.ClearTrackingParams
import io.github.nexalloy.morphe.twitter.link.unshorten.NoShortenedUrl
import io.github.nexalloy.morphe.twitter.misc.blockRedirectToXLite.BlockRedirectingToXLite
import io.github.nexalloy.morphe.twitter.misc.fab.HideFAB
import io.github.nexalloy.morphe.twitter.misc.recommendedusers.HideRecommendedUsers
import io.github.nexalloy.morphe.twitter.misc.roundOffNumbers.RoundOffNumbers
import io.github.nexalloy.morphe.twitter.misc.searchsuggestions.PauseSearchSuggestions
import io.github.nexalloy.morphe.twitter.misc.searchsuggestions.RemoveSearchSuggestions
import io.github.nexalloy.morphe.twitter.premium.enableForcePip.EnableForcePip
import io.github.nexalloy.morphe.twitter.premium.undoposts.EnableUndoPosts
import io.github.nexalloy.morphe.twitter.timeline.banner.HideBanner
import io.github.nexalloy.morphe.twitter.timeline.disableAutoScroll.DisableAutoScroll
import io.github.nexalloy.morphe.twitter.timeline.enableVidAutoAdvance.EnableVidAutoAdvance
import io.github.nexalloy.morphe.twitter.timeline.forceHD.ForceHD
import io.github.nexalloy.morphe.twitter.timeline.forceTranslate.ForceTranslate
import io.github.nexalloy.morphe.twitter.timeline.hideCommunityBadge.HideCommunityBadge
import io.github.nexalloy.morphe.twitter.timeline.hideCommunityNotes.HideCommunityNotes
import io.github.nexalloy.morphe.twitter.timeline.hideHiddenReplies.HideHiddenReplies
import io.github.nexalloy.morphe.twitter.timeline.hideNavbarBadges.HideNavBarBadges
import io.github.nexalloy.morphe.twitter.timeline.hidePostMetrics.HidePostMetrics
import io.github.nexalloy.morphe.twitter.timeline.hidePromoteButton.HidePromoteButton
import io.github.nexalloy.morphe.twitter.timeline.hideSocialProof.HideSocialProof
import io.github.nexalloy.morphe.twitter.timeline.live.HideLiveThreads
import io.github.nexalloy.morphe.twitter.timeline.removePremiumUpsell.RemovePremiumUpsell
import io.github.nexalloy.morphe.twitter.timeline.sensitivemediasettings.ShowSensitiveMedia
import io.github.nexalloy.morphe.twitter.timeline.showpollresults.ShowPollResults


val TwitterPatches: Array<Patch> = arrayOf(
    // Ads / recommendations
    HideAds,
    HideRecommendationItems,

    // Feature-flag driven toggles
    DisableChirpFont,

    // Links
    ClearTrackingParams,
    NoShortenedUrl,

    // Misc
    BlockRedirectingToXLite,
    HideFAB,
    HideRecommendedUsers,
    PauseSearchSuggestions,
    RemoveSearchSuggestions,
    RoundOffNumbers,

    // Premium
    EnableForcePip,
    EnableUndoPosts,

    // Timeline / tweet UI
    DisableAutoScroll,
    EnableVidAutoAdvance,
    ForceHD,
    ForceTranslate,
    HideBanner,
    HideCommunityBadge,
    HideCommunityNotes,
    HideHiddenReplies,
    HideLiveThreads,
    HideNavBarBadges,
    HidePostMetrics,
    HidePromoteButton,
    HideSocialProof,
    RemovePremiumUpsell,
    ShowSensitiveMedia,
    ShowPollResults,

)
