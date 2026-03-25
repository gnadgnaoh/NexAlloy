package io.github.nexalloy.morphe.reddit

import io.github.nexalloy.morphe.reddit.ad.general.HideAds
import io.github.nexalloy.morphe.reddit.misc.tracking.url.SanitizeUrlQuery

val RedditPatches = arrayOf(
    HideAds,
    SanitizeUrlQuery,
)