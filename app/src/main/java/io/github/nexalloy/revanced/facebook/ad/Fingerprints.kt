package io.github.nexalloy.revanced.facebook.ad

import io.github.nexalloy.morphe.findClassDirect
import io.github.nexalloy.morphe.findMethodDirect
import io.github.nexalloy.morphe.findMethodListDirect
import org.luckypray.dexkit.DexKitBridge
import org.luckypray.dexkit.query.enums.MatchType
import org.luckypray.dexkit.result.MethodData
import java.lang.reflect.Modifier

/**
 * Mirrors upstream's post-resolution filter used in resolveFeedCsrFilterMethods,
 * resolveLateFeedListHooks and resolveStoryPoolAddMethods: excludes constructors and
 * any method that is abstract, or declared on an interface/abstract class, since
 * those can't be hooked directly — Xposed needs the concrete implementing method.
 * DexKit's MethodData/ClassData expose `modifiers` directly from the dex, so this
 * can run entirely at fingerprint-resolution time (no classLoader needed).
 */
private fun MethodData.isConcreteHookTarget(): Boolean {
    if (isConstructor || Modifier.isAbstract(modifiers)) return false
    val ownerModifiers = declaredClass?.modifiers ?: return true
    return !Modifier.isInterface(ownerModifiers) && !Modifier.isAbstract(ownerModifiers)
}

// ─── Ad-kind enum ─────────────────────────────────────────────────────────────

val adKindEnumFingerprint = findClassDirect {
    findClass {
        matcher { usingEqStrings("AD", "UGC", "PARADE", "MIDCARD") }
    }.first()
}

// ─── Reels list-builder ───────────────────────────────────────────────────────
// Primary: class that logs "Non ads story fall into ads rendering logic"
// Fallback: structural signature (static 6-param void + static 5-param ArrayList)

val listBuilderClassFingerprint = findClassDirect {
    // Primary: structural — the class must contain methods matching ALL 6 shapes
    // below (mirrors upstream resolveListBuilderClass's `methods { Contains }` check
    // exactly). Only trusted when it resolves to a SINGLE unambiguous class.
    val structural = findClass {
        matcher {
            methods {
                matchType = MatchType.Contains
                add {
                    modifiers = Modifier.STATIC
                    returnType = "void"
                    paramTypes(null, null, null, null, null, "java.util.List")
                }
                add {
                    returnType = "void"
                    paramTypes(null, null, null, null, null, "java.util.List")
                }
                add {
                    modifiers = Modifier.STATIC
                    returnType = "java.util.ArrayList"
                    paramTypes(null, null, null, null, "boolean")
                }
                add {
                    modifiers = Modifier.STATIC
                    returnType = "java.util.ArrayList"
                    paramTypes(null, null, null, null, null, "boolean")
                }
                add {
                    returnType = "java.util.ArrayList"
                    paramTypes(null, null, null, "java.lang.Iterable")
                }
                add {
                    returnType = "java.util.List"
                    paramTypes(null, null, null, "boolean")
                }
            }
        }
    }

    // Fallback: string-based — only consulted when the structural search above is
    // ambiguous (0 or 2+ matches), exactly mirroring upstream's
    // `structuralCandidates.singleOrNull() ?: batchCandidates.firstOrNull() ?: error(...)`.
    structural.singleOrNull()
        ?: findClass {
            matcher { usingStrings("Non ads story fall into ads rendering logic, StoryType=%s, StoryId=%s") }
        }.firstOrNull()
        ?: error("Unable to resolve the upstream Facebook reels list-builder class")
}

// NOTE: listBuilderAppendFingerprint / listBuilderFactoryFingerprint were removed.
// Upstream now resolves these two methods via plain reflection + a scoring heuristic
// over every method on the already-resolved listBuilderClass (no rigid param-shape
// match), because Facebook occasionally ships variants with a different parameter
// count/order. That scoring logic needs a real java.lang.reflect.Method (List
// subtype checks via Class.isAssignableFrom), which only exists once classLoader is
// available — see resolveListBuilderAppendMethod / resolveListBuilderFactoryMethod
// in FacebookAdHelpers.kt, called from the patch body with
// ::listBuilderClassFingerprint.clazz (still DexKit-cached) as input.

// ─── Plugin packs ─────────────────────────────────────────────────────────────
// Upstream now blocks BOTH FbShortsViewerPluginPack AND MarketplaceAdsPluginPack.

val pluginPackMethodsFingerprint = findMethodListDirect {
    listOf("FbShortsViewerPluginPack", "MarketplaceAdsPluginPack").flatMap { tag ->
        findClass {
            matcher {
                methods {
                    add { returnType = "java.lang.String"; paramCount = 0; usingStrings(tag) }
                    add { returnType = "java.util.List"; paramCount = 0 }
                }
            }
        }.flatMap { cls ->
            cls.findMethod { matcher { returnType = "java.util.List"; paramCount = 0 } }
        }
    }.distinctBy { it.descriptor }.filter { !it.isConstructor }
}

// ─── Instream banner eligibility ─────────────────────────────────────────────
// Upstream resolves the CLASS first via a structural "0-arg String-returning method
// that uses this tag" shape (findClassesByZeroArgStringTags), then picks the actual
// boolean()/0-param eligibility method via plain reflection — preferring a non-static
// method declared on/inherited by that class, falling back to walking the superclass
// chain if none is found directly. That second part needs a real Class<*>
// (classLoader), so it lives in resolveInstreamBannerEligibilityMethod in
// FacebookAdHelpers.kt, called from the patch body with this class as input.

val instreamBannerEligibilityClassFingerprint = findClassDirect {
    findClass {
        matcher {
            methods {
                matchType = MatchType.Contains
                add { returnType = "java.lang.String"; paramCount = 0; usingStrings("InstreamAdIdleWithBannerState") }
            }
        }
    }.firstOrNull() ?: error("Unable to resolve the instream banner eligibility class")
}

// ─── Indicator pill eligibility ──────────────────────────────────────────────
// Upstream requires the CLASS to use BOTH strings (the render-path string and the
// fully-qualified plugin class name), then finds the static boolean(3-param) method
// inside that class — it doesn't require the method itself to reference either string.

val indicatorPillAdEligibilityFingerprint = findMethodDirect {
    val candidates = findClass {
        matcher {
            usingStrings(
                "IndicatorPillComponent.render",
                "com.facebook.feedback.comments.plugins.indicatorpill.reelsadsfloatingcta.ReelsAdsFloatingCtaPlugin"
            )
        }
    }
    candidates.firstNotNullOfOrNull { cls ->
        cls.findMethod {
            findFirst = true
            matcher { modifiers = Modifier.STATIC; returnType = "boolean"; paramCount = 3 }
        }.firstOrNull()
    } ?: error("Unable to resolve the Reels indicator pill ad eligibility method")
}

// ─── Reels banner render methods ─────────────────────────────────────────────

val reelsBannerRenderMethodsFingerprint = findMethodListDirect {
    listOf("ReelsBannerAdsComponent", "ReelsBannerAdsNativeComponent").flatMap { tag ->
        findMethod {
            matcher { paramCount = 1; usingStrings(tag) }
        }.filter { m -> !m.isConstructor }
    }.distinctBy { it.descriptor }
}

// ─── Profile Reels async ad query ─────────────────────────────────────────────

val profileReelsAsyncAdsQueryFingerprint = findMethodDirect {
    findMethod {
        matcher {
            returnType = "void"
            paramTypes(
                "com.facebook.auth.usersession.FbUserSession",
                "java.lang.Integer",
                "java.lang.Integer",
                "boolean"
            )
            usingStrings("ProfileReelsAsyncAdsQuery")
        }
    }.first { !it.isConstructor }
}

// ─── Feed CSR cache filter ────────────────────────────────────────────────────
// Upstream now also matches a newer 4-param variant — (FbUserSession, ?, ImmutableList, int) —
// in addition to the original 3-param (FbUserSession, ImmutableList, int) shape.
// We search both shapes per candidate class; HideFacebookAdsPatch derives the correct
// listArgIndex afterwards from each resolved Method's real parameter types.

val feedCsrFilterMethodsFingerprint = findMethodListDirect {
    listOf("FeedCSRCacheFilter", "FeedCSRCacheFilter2025H1", "FeedCSRCacheFilter2026H1", "FeedCSRCacheFilter2026H2").flatMap { tag ->
        findClass {
            matcher { usingStrings(tag) }
        }.flatMap { cls ->
            // NOTE: older builds returned the filtered ImmutableList directly. Current
            // builds return a result WRAPPER instead — e.g.
            //   AnH(FbUserSession, <ctx>, ImmutableList, int) -> LX/2iE
            // where the filtered list sits in a field of that wrapper. Pinning
            // returnType to ImmutableList therefore matched NOTHING and the whole feed
            // CSR filter hook silently never installed (runCatching swallowed it),
            // which is why sponsored items still reached the profile feed.
            // We no longer constrain the return type at all; the hook only needs the
            // ImmutableList PARAMETER, which it rewrites in beforeHookedMethod. The
            // param shape plus the class-level tag string is specific enough.
            val fourParam = cls.findMethod {
                matcher {
                    paramTypes(
                        "com.facebook.auth.usersession.FbUserSession",
                        null,
                        "com.google.common.collect.ImmutableList",
                        "int"
                    )
                }
            }
            if (fourParam.isNotEmpty()) fourParam else cls.findMethod {
                matcher {
                    paramTypes(
                        "com.facebook.auth.usersession.FbUserSession",
                        "com.google.common.collect.ImmutableList",
                        "int"
                    )
                }
            }
        }
    }.distinctBy { it.descriptor }.filter { it.isConcreteHookTarget() }
}

// ─── Late feed list sanitisers ────────────────────────────────────────────────

val lateFeedListMethodsFingerprint = findMethodListDirect {
    val results = ArrayList<org.luckypray.dexkit.result.MethodData>()

    findClass { matcher { usingStrings("handleStorageStories", "Empty Storage List") } }.forEach { cls ->
        cls.findMethod {
            matcher { returnType = "void"; paramTypes(null, "com.google.common.collect.ImmutableList", "int") }
        }.forEach { results.add(it) }
    }

    findClass { matcher { usingStrings("cancelVendingTimerAndAddToPool_") } }.forEach { cls ->
        cls.findMethod {
            matcher { returnType = "void"; paramTypes("com.google.common.collect.ImmutableList", "java.lang.String") }
        }.forEach { results.add(it) }
    }

    listOf("CSRNoOpStorageLifecycleImpl", "FeedCSRStorageLifecycle", "FriendlyFeedCSRStorageLifecycle", "FbShortsCSRStorageLifecycle").forEach { tag ->
        findClass { matcher { usingStrings(tag) } }.forEach { cls ->
            // 3-param variant, e.g. AAB(FbUserSession, <ctx>, ImmutableList)
            cls.findMethod {
                matcher {
                    returnType = "void"
                    paramTypes("com.facebook.auth.usersession.FbUserSession", null, "com.google.common.collect.ImmutableList")
                }
            }.forEach { results.add(it) }
            // 4-param variant, e.g. AAA(FbUserSession, <ctx>, <ctx>, ImmutableList).
            // Present on the FriendlyFeed (professional-mode profile) lifecycle and
            // previously unhooked, letting sponsored stories through on that surface.
            cls.findMethod {
                matcher {
                    returnType = "void"
                    paramTypes("com.facebook.auth.usersession.FbUserSession", null, null, "com.google.common.collect.ImmutableList")
                }
            }.forEach { results.add(it) }
            // 1-param variant, e.g. AFq(ImmutableList) on the FriendlyFeed lifecycle.
            cls.findMethod {
                matcher {
                    returnType = "void"
                    paramTypes("com.google.common.collect.ImmutableList")
                }
            }.forEach { results.add(it) }
        }
    }

    results.distinctBy { it.descriptor }.filter { it.isConcreteHookTarget() }
}

// ─── Story pool add ───────────────────────────────────────────────────────────

/**
 * Pools and queues that admit a story into an ad slot.
 *
 * Safe to widen freely: the hook that consumes this is item-aware — it inspects the story
 * being offered and only refuses one it can positively identify as sponsored. A tag that
 * turns out to hold organic stories therefore costs nothing, which is why the Shorts and
 * Friendly-feed sponsored pools are included even though their exact semantics were never
 * confirmed at runtime.
 */
val STORY_POOL_TAGS = listOf(
    "CSRStoryPoolCoordinator",
    "FeedStoryPoolCoordinator",
    "FbShortsSponsoredPool",
    "FBShortsSponsoredPool",
    "FbShortsIFUSponsoredPool",
    "FriendlyFeedSponsoredPool",
    "FbShortsCSRSponsoredSlotQueue",
    // NOT "FbShortsCSRCacheFilter". It looks like a pool and has the same method shape,
    // but it is the Shorts CACHE ELIGIBILITY filter: its boolean methods answer "may this
    // story appear in the Shorts tray at all", for organic stories as much as for ads.
    // Refusing there empties the whole tray — the feed Reels row renders as a permanently
    // blank card. Item-awareness does not save it, because the question the method asks
    // is not "should this ad go in an ad slot".
)

val storyPoolAddMethodsFingerprint = findMethodListDirect {
    STORY_POOL_TAGS.flatMap { tag ->
        findClass { matcher { usingStrings(tag) } }.flatMap { cls ->
            cls.findMethod { matcher { returnType = "boolean"; paramCount = 1 } }
        }
    }.distinctBy { it.descriptor }.filter { it.isConcreteHookTarget() }
}

// ─── Sponsored pool ───────────────────────────────────────────────────────────
// Upstream requires the CLASS to use BOTH strings, then verifies the
// boolean(GraphQLFeedUnitEdge) method shape exists somewhere in that class.

val sponsoredPoolClassFingerprint = findClassDirect {
    val candidates = findClass {
        matcher { usingEqStrings("SponsoredPoolContainerAdapter", "Edge type mismatch; not added") }
    }
    candidates.firstOrNull { cls ->
        cls.findMethod {
            matcher { returnType = "boolean"; paramTypes("com.facebook.graphql.model.GraphQLFeedUnitEdge") }
        }.isNotEmpty()
    } ?: error("Unable to resolve the Facebook sponsored pool class")
}

val sponsoredPoolAddMethodFingerprint = findMethodDirect {
    sponsoredPoolClassFingerprint().findMethod {
        matcher { returnType = "boolean"; paramTypes("com.facebook.graphql.model.GraphQLFeedUnitEdge") }
    }.single()
}

// ─── Sponsored story manager ──────────────────────────────────────────────────
// Upstream requires the CLASS to use BOTH strings, then verifies the
// GraphQLFeedUnitEdge()/0-param method shape exists somewhere in that class.

val sponsoredStoryManagerClassFingerprint = findClassDirect {
    val candidates = findClass {
        matcher { usingEqStrings("FeedSponsoredStoryHolder.onPositionReset", "freshFeedStoryHolder") }
    }
    candidates.firstOrNull { cls ->
        cls.findMethod {
            matcher { returnType = "com.facebook.graphql.model.GraphQLFeedUnitEdge"; paramCount = 0 }
        }.isNotEmpty()
    } ?: error("Unable to resolve the Facebook sponsored story manager class")
}

val sponsoredStoryNextMethodFingerprint = findMethodDirect {
    sponsoredStoryManagerClassFingerprint().findMethod {
        matcher { returnType = "com.facebook.graphql.model.GraphQLFeedUnitEdge"; paramCount = 0 }
    }.single()
}

// ─── Story ads in-disc source ─────────────────────────────────────────────────
// Upstream changed search string to "ads_deletion" (from commit fixing profile timeline ads)

val storyAdsInDiscClassFingerprint = findClassDirect {
    findMethod {
        matcher { usingStrings("ads_deletion") }
    }.first { md ->
        val cls = md.declaredClass ?: return@first false
        cls.findMethod {
            matcher {
                returnType = "com.google.common.collect.ImmutableList"
                paramTypes("com.facebook.auth.usersession.FbUserSession", null, "com.google.common.collect.ImmutableList")
            }
        }.isNotEmpty() && cls.findMethod {
            matcher { returnType = "void"; paramTypes(null, "com.google.common.collect.ImmutableList") }
        }.isNotEmpty()
    }.declaredClass!!
}

/**
 * The specific 0-param void method inside storyAdsInDiscClass that triggers ad insertion.
 * Upstream finds this via usingStrings("ads_insertion") — we replicate that here.
 */
val storyAdsInsertionTriggerMethodFingerprint = findMethodDirect {
    storyAdsInDiscClassFingerprint().findMethod {
        matcher {
            returnType = "void"
            paramCount = 0
            usingStrings("ads_insertion")
        }
    }.firstOrNull()
        ?: storyAdsInDiscClassFingerprint().findMethod {
            // Fallback: first 0-param void method if string not found (obfuscated builds)
            matcher { returnType = "void"; paramCount = 0 }
        }.first()
}

// ─── Game ad request methods ──────────────────────────────────────────────────

val gameAdRequestMethodsFingerprint = findMethodListDirect {
    listOf(
        "Invalid JSON content received by onGetInterstitialAdAsync: ",
        "Invalid JSON content received by onGetRewardedInterstitialAsync: ",
        "Invalid JSON content received by onRewardedVideoAsync: ",
        "Invalid JSON content received by onLoadAdAsync: ",
        "Invalid JSON content received by onShowAdAsync: "
    ).flatMap { tag ->
        findMethod {
            matcher { returnType = "void"; paramTypes("org.json.JSONObject"); usingStrings(tag) }
        }
    }.distinctBy { it.descriptor }.filter { !it.isConstructor }
}

// ─── Feed collection edge filter ──────────────────────────────────────────────
// Replaces FB571_FEED_COLLECTION_TARGETS (was pinned to X.1vr). "addNewEdgeToCollection"
// is one of the very few feed methods that survives ProGuard with its real name, so it
// can be matched by name + shape on any build. Verified on FB 573:
//   X.1vy.addNewEdgeToCollection(ImmutableList$Builder, GraphQLFeedUnitEdge, X.1cS): boolean
val feedCollectionAddEdgeMethodFingerprint = findMethodDirect {
    val byShape = findMethod {
        matcher {
            name = "addNewEdgeToCollection"
            returnType = "boolean"
            paramTypes(null, "com.facebook.graphql.model.GraphQLFeedUnitEdge", null)
        }
    }.filter { it.isConcreteHookTarget() }

    byShape.firstOrNull()
        // Looser fallback: any concrete addNewEdgeToCollection that takes an edge
        // somewhere in its parameter list (param count/order occasionally shifts).
        ?: findMethod {
            matcher { name = "addNewEdgeToCollection"; returnType = "boolean" }
        }.first {
            it.isConcreteHookTarget() &&
                it.paramTypeNames.any { p -> p == "com.facebook.graphql.model.GraphQLFeedUnitEdge" }
        }
}

// ─── Story ad source providers (all of them) ──────────────────────────────────
// Upstream pinned SIX provider classes by name (FB571_STORY_AD_SOURCE_CLASSES) because
// the single-class DexKit lookup missed the split pipelines. This returns EVERY class
// that both logs "ads_deletion" and carries the provider shape, so no name is needed.
// Verified on FB 573: three classes log "ads_deletion", exactly one carries the shape.
val storyAdsInDiscMethodsFingerprint = findMethodListDirect {
    findMethod {
        matcher { usingStrings("ads_deletion") }
    }.filter { md ->
        val cls = md.declaredClass ?: return@filter false
        cls.findMethod {
            matcher {
                returnType = "com.google.common.collect.ImmutableList"
                paramTypes("com.facebook.auth.usersession.FbUserSession", null, "com.google.common.collect.ImmutableList")
            }
        }.isNotEmpty() && cls.findMethod {
            matcher { returnType = "void"; paramTypes(null, "com.google.common.collect.ImmutableList") }
        }.isNotEmpty()
    }.distinctBy { it.declaredClass?.name }
}

// ─── Video plugin system: packs, descriptors, static builders ─────────────────
//
// Everything below targets the layer that serves ads INSIDE a video, as opposed to ads
// that arrive as their own feed story. A runtime trace established that this layer, and
// not the ad-break subsystem, is what delivers the sponsored clip that replaces a
// creator's video and the sponsored card that sits under it: with 21 ad-break resolver
// accessors and 50 ad-break state machine methods hooked, not one of them was ever called
// while those ads were on screen.
//
// None of these fingerprints pin a pack or descriptor name. They resolve the SHAPE of the
// plugin API and let the hooks decide per instance, because some ad packs assemble their
// name at runtime and can never be matched by a literal.

/**
 * Every plugin-list getter in the video plugin system.
 *
 * Resolved by shape from a known pack rather than by method name: the 0-argument List
 * getter that plugin packs expose. Includes getters inherited from a shared base, which
 * ad packs and organic packs use in common — hence the per-instance filtering in
 * [hookPluginPackList].
 */
val allPluginPackListMethodsFingerprint = findMethodListDirect {
    val seed = listOf("FbShortsViewerPluginPack", "MarketplaceAdsPluginPack", "AdBreakPluginPack")
        .firstNotNullOfOrNull { tag ->
            runCatching {
                findClass {
                    matcher {
                        methods {
                            add { returnType = "java.lang.String"; paramCount = 0; usingStrings(tag) }
                            add { returnType = "java.util.List"; paramCount = 0 }
                        }
                    }
                }.firstOrNull()
            }.getOrNull()
        } ?: error("No plugin pack to seed the list-getter shape from")

    val getter = seed.methods.firstOrNull {
        it.paramTypeNames.isEmpty() && it.returnTypeName == "java.util.List"
    } ?: error("Plugin pack list getter shape not found")

    findMethod {
        matcher { name = getter.name; paramCount = 0; returnType = "java.util.List" }
    }.filter { it.isConcreteHookTarget() }.distinctBy { it.descriptor }
}

/**
 * The eligibility gate shared by every video plugin descriptor — the boolean the player
 * calls to ask a descriptor whether it applies to the current video.
 *
 * Shape is learnt from a descriptor known to be ads-only, so the obfuscated method name is
 * never pinned. There are many implementations (165 on the build this was written
 * against), which is exactly why [hookPluginDescriptorGate] filters per instance instead
 * of trying to fingerprint the ad ones.
 */
val pluginDescriptorGateMethodsFingerprint = findMethodListDirect {
    val seed = listOf("PlayableAdOverlayPluginDescriptor", "AdsSmartOverlayPluginDescriptor")
        .firstNotNullOfOrNull { tag ->
            runCatching { findClass { matcher { usingStrings(tag) } }.firstOrNull() }.getOrNull()
        } ?: error("No ad plugin descriptor to seed the gate shape from")

    val gate = seed.methods.firstOrNull {
        it.returnTypeName == "boolean" &&
            it.paramTypeNames.size == 4 &&
            it.paramTypeNames[1] == "com.facebook.video.common.playerorigin.PlayerOrigin"
    } ?: error("Plugin descriptor gate shape not found")

    findMethod {
        matcher {
            name = gate.name
            returnType = "boolean"
            paramTypes(null, "com.facebook.video.common.playerorigin.PlayerOrigin", null, null)
        }
    }.filter { it.isConcreteHookTarget() }.distinctBy { it.descriptor }
}

/**
 * Direct-monetization ad plugins — the in-video ads a creator monetises with.
 *
 * These come from a plain static builder rather than from a pack object, so neither a
 * pack-level nor a descriptor-level hook reaches them; the builder is matched by the one
 * literal it carries.
 */
val directMonetizationAdsPluginListFingerprint = findMethodListDirect {
    findMethod {
        matcher {
            returnType = "com.google.common.collect.ImmutableList"
            usingStrings("REELS_DIRECT_MONETIZATION_ADS")
        }
    }.filter { it.isConcreteHookTarget() }.distinctBy { it.descriptor }
}


// ─── Ad-only Litho components ─────────────────────────────────────────────────
//
// A component that exists solely to draw an advertisement can be suppressed by making its
// render return nothing; Litho treats a null layout as "draw nothing". This is a blunt
// instrument, so three guards stand in front of it.

/**
 * Return types that prove a 1-argument method is NOT a render.
 *
 * Facebook ships generated string-table classes with signatures like `A00(int): String`
 * that mention nearly every tag in the app. Without this filter, adding tags below would
 * hook those and corrupt unrelated text. A render always returns a Component or a Section.
 */
private val NON_RENDER_RETURN_TYPES = setOf(
    "java.lang.String", "void", "boolean", "int", "long", "float", "double", "char", "byte", "short"
)

/**
 * Components that render ORGANIC content. Any class referencing one of these is shared
 * infrastructure, not an ad component, even when it also mentions an ad component name —
 * Facebook's generated feed components carry several names at once.
 *
 * This guard is not theoretical. "SponsoredNewsFeedUnitComponent" reads like an ad and is
 * still in the tag list below, but on this build it resolves to the generic news feed
 * story component; suppressing it leaves the feed stuck on its loading skeleton forever.
 * The guard catches it by name-independent means, so the mistake cannot be repeated by
 * adding a plausible-looking tag.
 */
private val ORGANIC_COMPONENT_MARKERS = listOf(
    "NewsFeedFeedUnitComponent",
    "FeedGraphQLStoryRootComponent",
    "FeedNonGraphQLRootStoryComponent",
    "FeedStoryUFIFeedbackSummaryComponent",
    "InlineComposerV2RootComponent",
    "ReactFeedStoryComponent",
    // Reels / Shorts in-feed unit. Added after a regression: an "…AdsMedia…" tag resolved
    // to the class that also renders the tray's body wrapper, and the feed Reels row went
    // blank. Anything rendering these is shared, whatever its ad-sounding tag suggests.
    "ShowcaseFbShortsBodyWrapperComponent",
    "ShowcaseFbShortsRootComponent",
    "FbShortsIfuTileComponent",
)

private fun MethodData.isRenderShaped(): Boolean =
    !isConstructor && returnTypeName !in NON_RENDER_RETURN_TYPES

private fun DexKitBridge.rejectSharedFeedComponents(methods: List<MethodData>): List<MethodData> {
    val shared = ORGANIC_COMPONENT_MARKERS.flatMapTo(mutableSetOf()) { marker ->
        runCatching { findClass { matcher { usingStrings(marker) } }.map { it.name } }
            .getOrDefault(emptyList())
    }
    return methods.filter { it.className !in shared }
}

/**
 * The obfuscated Litho render return type for this build, derived rather than pinned: it
 * is simply the return type of a render method already located by string. Both the
 * Component and the Section flavour are resolved this way.
 */
private fun DexKitBridge.renderReturnTypeFrom(seedTags: List<String>): String? =
    seedTags.firstNotNullOfOrNull { tag ->
        runCatching {
            findClass { matcher { usingStrings(tag) } }
                .flatMap { cls -> cls.methods.filter { it.paramTypeNames.size == 1 } }
                .firstOrNull { it.isRenderShaped() }
                ?.returnTypeName
        }.getOrNull()
    }

/**
 * Resolves the render method of ad-only components identified by [tags].
 *
 * Matching is at CLASS level, not method level. Matching the tag on the render method
 * itself misses every component whose tag literal lives in a sibling method — the
 * in-player banner is exactly that: its render carries no string at all, the tag sits in
 * an eleven-argument setup method on the same class.
 *
 * Class-level matching is looser, so the result is narrowed twice: the method must return
 * the render type derived above (which excludes the string-table classes), and
 * [rejectSharedFeedComponents] drops anything that also renders organic content.
 */
private fun DexKitBridge.adRenderMethodsFor(tags: List<String>, seedTags: List<String>): List<MethodData> {
    val renderType = renderReturnTypeFrom(seedTags) ?: return emptyList()
    val methods = tags.flatMap { tag ->
        runCatching {
            findClass { matcher { usingStrings(tag) } }.flatMap { cls ->
                cls.findMethod { matcher { paramCount = 1; returnType = renderType } }
            }
        }.getOrDefault(emptyList())
    }.filter { it.isRenderShaped() }.distinctBy { it.descriptor }
    return rejectSharedFeedComponents(methods)
}

val AD_SURFACE_RENDER_TAGS = listOf(
    // AdBreak (in-stream video) (11)
    "AdBreakCallToActionButtonComponent",
    "AdBreakContextCardComponent",
    "AdBreakContextCardSponsorInfoComponent",
    "AdBreakContextStoryOverlayComponent",
    "AdBreakCountdownWithTextComponent",
    "AdBreakDeferredCTACardComponent",
    "AdBreakDeferredCTAPoliticalAdSponsorInfoComponent",
    "AdBreakInPlayerAnimatedSingleImageComponent",
    "AdBreakNonInterruptiveCardComponent",
    "AdBreakPostRollEndingScreenComponent",
    "AdBreakTransitionWithAnimationComponent",
    // Other (8)
    "AdsSocialContextComponent",
    "AdsTextOverlay",
    "BKBloksAdsUgcPermalinkPostTextComponent",
    "FacecastLiveVideoAdsStatusPillComponent",
    "InContentAdsHeaderPillCountdownTimerComponent",
    "InContentAdsSidebarCountdownTimerComponent",
    "SponsoredNewsFeedUnitComponent",
    "bk.action.HideAdsOverlay",
    // Carousel / horizontal scroll (3)
    "CarouselAdsAttachmentHScrollComponent",
    "FbAdsHscrollFooterComponent",
    "FbAdsHscrollItemComponent",
    // Multi-ads card (5)
    "FBMultiAdsFeedUnitKComponent",
    "MultiAdsAdCardFooterKComponent",
    "MultiAdsAdCardHeaderKComponent",
    "MultiAdsAdCardMediaKComponent",
    "MultiAdsAndBrowseFallbackKComponent",
    // Shorts / Reels (50)
    "FbShortsAdsAuthorProfilePictureComponent",
    "FbShortsAdsAuthorWithFDSComponent",
    "FbShortsAdsCTAKComponent",
    "FbShortsAdsCTAStickerComponent",
    "FbShortsAdsCTMEditableEndSceneKComponent",
    "FbShortsAdsCreativeProductStickerCTAComponent",
    "FbShortsAdsCreativeStickerImageComponent",
    "FbShortsAdsDLPProductCardComponent",
    "FbShortsAdsDotsCarouselPlayerComponent",
    "FbShortsAdsHScrollComponent",
    "FbShortsAdsHscrollAlbumLastCardComponent",
    "FbShortsAdsIABFragmentWrapperComponent",
    "FbShortsAdsIABReentryMidsceneCardComponent",
    "FbShortsAdsIABScreenshotEndSceneComponent",
    "FbShortsAdsLeadGenFirstQuestionComponent",
    "FbShortsAdsLeadGenPIIComponent",
    "FbShortsAdsLeadGenSAQComponent",
    "FbShortsAdsMidSceneBizAgentComponent",
    "FbShortsAdsMidSceneSiteExtensionComponent",
    "FbShortsAdsMidsceneCardComponent",
    "FbShortsAdsMidsceneContainerComponent",
    "FbShortsAdsMixedMediaCardKComponent",
    "FbShortsAdsMixedMediaTileComponent",
    "FbShortsAdsMultiAdsGridCardComponent",
    "FbShortsAdsMultiAdsGridComponent",
    "FbShortsAdsMultiAdsVerticalCardComponent",
    "FbShortsAdsMultiAdsVerticalComponent",
    "FbShortsAdsNativeSlideshowImageComponent",
    "FbShortsAdsNativeSlideshowPlayerComponent",
    "FbShortsAdsPhotoCardComponent",
    "FbShortsAdsPhotoKComponent",
    "FbShortsAdsPostScrollNudgeBizAiAgentComponent",
    "FbShortsAdsPostScrollNudgeLeadGenPIIComponent",
    "FbShortsAdsPostScrollNudgeLeadGenSAQComponent",
    "FbShortsAdsPostScrollNudgeLeadGenSingleSelectComponent",
    "FbShortsAdsPostScrollNudgeScreenShotComponent",
    "FbShortsAdsPostScrollNudgeTrustSignalComponent",
    "FbShortsAdsRealTimeIntentComponent",
    "FbShortsAdsRtiSingleCardKComponent",
    "FbShortsAdsStickerCTAComponent",
    "FbShortsAdsSwipeLeftComponent",
    "FbShortsAdsXAndBrowseProgressRingComponent",
    "FbShortsAdsXAndBrowseStartingIndicatorComponent",
    "FbShortsImageAdsTextOverlayKComponent",
    "FbShortsShoppableAdsItemComponent",
    "FbShortsVideoAdsTextOverlayKComponent",
    "FbShortsViewerVideoAdsMusicComponent",
    "FbShortsViewerVideoSponsorLabelComponent",
    "ReelsAdsCaptionCommentComponent",
    // NOT "ShowcaseFbShortsAdsMediaComponent". It resolves to a class that also renders
    // ShowcaseFbShortsBodyWrapperComponent and carries "fb_shorts_ifu_tile" — the Reels
    // in-feed unit tile. Suppressing its render leaves the Reels row in the feed as a
    // blank card that never fills in. The marker list below now catches it structurally
    // as well, but the tag is dropped too.
    // Search results (6)
    "SearchResultsSponsoredStoryBloksCaptionComponent",
    "SearchResultsSponsoredStoryBloksFooterLithoComponent",
    "SearchResultsSponsoredStoryComponent",
    "SearchResultsSponsoredStoryContentComponent",
    "SearchResultsSponsoredStoryHeaderComponent",
    "SearchResultsSponsoredStoryMultiShareItemComponent",
    // Stories viewer (11)
    "StoryViewerAdsBackgroundImageComponent",
    "StoryViewerAdsCardStyleMediaComponent",
    "StoryViewerAdsCollectionPhotoComponent",
    "StoryViewerAdsExpandableCaptionComponent",
    "StoryViewerAdsExpandableCarouselOptInComponent",
    "StoryViewerAdsFollowBySocialContextComponent",
    "StoryViewerAdsMultiPartComponent",
    "StoryViewerAdsOptInComponent",
    "StoryViewerAdsProductHighlightPhotoComponent",
    "StoryViewerAdsRootContainerComponent",
    "StoryViewerAdsTopBarComponent",
    // Video ads CTA / attachment (8)
    "VideoAdsActionComponent",
    "VideoAdsAttachmentFooterComponent",
    "VideoAdsAttachmentFooterTextOptimizedComponent",
    "VideoAdsButtonComponent",
    "VideoAdsCallToActionAttachmentActionButtonComponent",
    "VideoAdsCallToActionComponent",
    "VideoAdsCallToActionDelayedWrapperComponent",
    "VideoAdsPageLikeCallToActionComponent",
    // Watch immersive (3)
    "WatchSponsoredImmersiveAttachmentCallToActionComponent",
    "WatchSponsoredImmersiveAttachmentFooterComponent",
    "WatchSponsoredImmersiveHeaderComponent",
)

val AD_SECTION_TAGS = listOf(
    "AdsCommentSection",
    "BizDiscoCollageSponsoredSection",
)

val adSurfaceRenderMethodsFingerprint = findMethodListDirect {
    adRenderMethodsFor(
        AD_SURFACE_RENDER_TAGS,
        seedTags = listOf("ReelsBannerAdsComponent", "FbShortsAdsRootKComponent.render", "AdBreakContextCardComponent"),
    )
}

/**
 * Litho Sections rather than Components — a different return type, same idea.
 * The user's own "Ad Activity" history screen is deliberately excluded: it is a settings
 * surface for reviewing ads, not an advertisement.
 */
val adSectionRenderMethodsFingerprint = findMethodListDirect {
    adRenderMethodsFor(AD_SECTION_TAGS, seedTags = AD_SECTION_TAGS)
}


// ─── Stories ads ──────────────────────────────────────────────────────────────
// Sponsored slides between friends' stories. Anchored on a real, unobfuscated class name
// rather than on anything version-specific.

private const val AD_STORY_CLASS = "com.facebook.audience.snacks.model.AdStory"

/**
 * Litho components holding an [AD_STORY_CLASS] field — the caption, label, CTA and overlay
 * pieces of a story ad. Structural rather than tag-based, so it needs no per-component
 * list and does not go stale when Facebook renames things.
 */
val storyAdComponentRenderMethodsFingerprint = findMethodListDirect {
    val renderType = renderReturnTypeFrom(
        listOf("ReelsBannerAdsComponent", "FbShortsAdsRootKComponent.render")
    ) ?: return@findMethodListDirect emptyList()

    val methods = runCatching {
        findClass {
            matcher { fields { add { type = AD_STORY_CLASS } } }
        }.flatMap { cls ->
            cls.findMethod { matcher { paramCount = 1; returnType = renderType } }
        }
    }.getOrDefault(emptyList()).filter { it.isRenderShaped() }.distinctBy { it.descriptor }

    rejectSharedFeedComponents(methods)
}

/**
 * The Stories ad pagination fetch — stops story ads being requested at all, which is
 * cheaper and less visible than removing them once they have arrived.
 */
val storiesAdsPaginationMethodFingerprint = findMethodListDirect {
    findMethod {
        matcher {
            paramTypes(
                "com.facebook.auth.usersession.FbUserSession",
                null,
                "com.google.common.collect.ImmutableList",
                "com.google.common.collect.ImmutableList"
            )
            usingStrings("FBStoriesAdsPaginatingQuery")
        }
    }.filter { it.isConcreteHookTarget() }.distinctBy { it.descriptor }
}

/**
 * Methods of the timeline story component that receive a story. Read-only in the
 * diagnostics patch — this class also renders organic posts and must never be suppressed.
 */
val timelineStoryComponentMethodsFingerprint = findMethodListDirect {
    findClass {
        matcher { usingStrings("sponsored_timeline_stories_test_key") }
    }.flatMap { cls ->
        cls.methods.filter { m ->
            !m.isConstructor && m.paramTypeNames.isNotEmpty() && m.paramTypeNames.size <= 4
        }
    }.filter { it.isConcreteHookTarget() }.distinctBy { it.descriptor }
}

/**
 * Every adapter that produces a timeline story from a list entry.
 *
 * This replaced a first attempt that looked for List-returning methods on classes carrying
 * timeline name literals: that resolved to nothing at all, because the timeline's data
 * plumbing lives in obfuscated classes which reference their query by string-table index
 * rather than by literal. Going through the story type instead works regardless of how
 * the surrounding classes are named.
 *
 * Each call converts one timeline entry, so logging the category of the result shows every
 * story the profile page assembles, and the parameter type reveals what the underlying
 * list holds — which is what a real filter would need to hook.
 */
val timelineStoryAdapterMethodsFingerprint = findMethodListDirect {
    val storyType = timelineStoryTypeName() ?: return@findMethodListDirect emptyList()
    findMethod {
        matcher { paramCount = 1; returnType = storyType }
    }.filter { it.isConcreteHookTarget() }.distinctBy { it.descriptor }
}

/**
 * The profile timeline story component's render.
 *
 * Suppressing this component wholesale blanks the entire "all posts" section — it draws
 * organic posts, customised stories and featured highlights as well as ads — so the hook
 * that uses this decides per story rather than per component.
 */
val timelineStoryRenderMethodFingerprint = findMethodDirect {
    val renderType = renderReturnTypeFrom(
        listOf("ReelsBannerAdsComponent", "FbShortsAdsRootKComponent.render")
    ) ?: error("Litho render type not found")

    findClass {
        matcher { usingStrings("sponsored_timeline_stories_test_key") }
    }.flatMap { cls ->
        cls.findMethod { matcher { paramCount = 1; returnType = renderType } }
    }.first { it.isConcreteHookTarget() }
}
