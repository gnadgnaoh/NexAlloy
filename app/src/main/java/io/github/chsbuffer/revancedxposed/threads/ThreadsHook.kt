package io.github.chsbuffer.revancedxposed.threads

import io.github.chsbuffer.revancedxposed.threads.ads.HideAds
import io.github.chsbuffer.revancedxposed.threads.network.BlockNetwork
import io.github.chsbuffer.revancedxposed.threads.tracking.SanitizeTrackingLinks

val ThreadsPatches = arrayOf(HideAds, BlockNetwork, SanitizeTrackingLinks)
