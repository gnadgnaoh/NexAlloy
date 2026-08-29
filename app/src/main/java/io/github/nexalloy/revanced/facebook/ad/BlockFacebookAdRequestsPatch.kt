package io.github.nexalloy.revanced.facebook.ad

import io.github.nexalloy.patch
import io.github.nexalloy.revanced.facebook.hookAdQueryFetch
import io.github.nexalloy.revanced.facebook.hookAdRequestNoOp
import io.github.nexalloy.revanced.facebook.hookEmptyCollectionResult
import io.github.nexalloy.revanced.facebook.hookForceBoolean

/**
 * Stops advertisements being requested, rather than removing them once they arrive.
 *
 * The rest of this module works downstream: it watches lists of stories go past and takes
 * out the ones it can prove are sponsored. That works, but it inherits a hard problem —
 * every filter has to recognise an ad, and every regression this module has had came from
 * a recognition test answering "yes" to something organic. This patch avoids the question
 * entirely. A request that is never made returns nothing to recognise.
 *
 * It is a separate toggle from [HideFacebookAds] because it acts on different code at a
 * different moment, and because its failure mode is different too: where a bad filter
 * blanks a surface, a bad request hook leaves a surface waiting for data that never
 * comes. Turning this off restores every fetch while the filters keep running.
 *
 * Nine pipelines, each with no coverage before:
 *
 *  - **The news feed's async ad channel.** The big one. Ads no longer ride along with the
 *    feed response; they are fetched separately and spliced in as they land, which is
 *    precisely why the CSR filter and the sponsored pool — both of which only ever see
 *    the feed response — never saw them.
 *  - **The Stories viewer's ad pagination.** The newer of the two paginating sources,
 *    the one that does not log "ads_deletion" and so was missed by the provider hooks.
 *  - **The Reels video-ad fetch.** Tops up the ad supply for the main Reels viewer.
 *  - **Real-time-intent insertion in Video Home.** Placement rather than fetch: the ad
 *    exists, this is the step that puts it in the list you scroll.
 *  - **The position-one feed ad.** The advert in the first slot of the news feed, which
 *    gets its slot from a session budget rather than from the feed ranker.
 *  - **The ad-channel network layer.** One level below the async-ad controller. The
 *    controller hooks stop it *deciding* to request; these stop the request reaching the
 *    wire, which matters because the feed, Reels and the mid-session sponsored-story
 *    top-up each get there through callers the controller does not own.
 *  - **The rest of the Video Home / Reels pipeline.** The fetch, the general insertion
 *    step and the delayed real-time-intent render, plus the mid-card ad survey. The
 *    existing hook covers one insertion point for one ad kind; this covers the routes
 *    every other Reels ad takes.
 *  - **The Stories viewer's payload fetch.** A second method, separate from the one
 *    already blocked, through which the viewer was still topping up its ad buckets.
 *  - **The search "AI mode" ad story query.** Its sibling query — the one that decides
 *    *which* ads to show — was already blocked; this is the one that fetches the story
 *    behind an ad that has already been chosen, so ads could still hydrate from cache.
 *
 * Every hook is shape-checked before it is installed. A `void` method is skipped, a
 * method returning a list is given an empty one, a boolean gate is answered false; a
 * method whose return type does not match what the hook can produce is left alone rather
 * than handed a null it would crash on.
 */
val BlockFacebookAdRequests = patch(
    name = "Block Facebook ad requests",
    description = "Stops the feed, Stories, Reels and Watch asking for ads in the first place, instead of removing them afterwards. Turn off if a feed or the Stories viewer stops loading.",
) {

    // ── 1. News feed async ad channel ────────────────────────────────────────
    //
    // Request and response are both hooked. The request alone is not enough: the
    // channel is warmed by a prefetch that can run before these hooks are installed,
    // so a response already in flight would still be spliced into the feed.

    runCatching { ::feedAsyncAdRequestMethodsFingerprint.dexMethodList }.getOrNull().orEmpty()
        .forEach { dm -> runCatching { hookAdRequestNoOp(dm.toMethod()) } }

    runCatching { ::feedAsyncAdResultMethodsFingerprint.dexMethodList }.getOrNull().orEmpty()
        .forEach { dm -> runCatching { hookEmptyCollectionResult(dm.toMethod()) } }

    // ── 2. Stories viewer ad pagination ──────────────────────────────────────
    //
    // Only the fetches are stopped. The method that assembles the buckets to show is
    // deliberately left alone: it returns the list the viewer actually pages through,
    // organic stories included, and emptying it would end the story session rather
    // than remove the ads from it.

    runCatching { ::storyViewerAdsFetchMethodsFingerprint.dexMethodList }.getOrNull().orEmpty()
        .forEach { dm -> runCatching { hookAdRequestNoOp(dm.toMethod()) } }

    // ── 3. Reels video ads ───────────────────────────────────────────────────

    runCatching { ::reelsVideoAdsFetchMethodsFingerprint.dexMethodList }.getOrNull().orEmpty()
        .forEach { dm -> runCatching { hookAdRequestNoOp(dm.toMethod()) } }

    // ── 4. Video Home real-time-intent ad insertion ──────────────────────────

    runCatching { ::videoHomeAdInsertionMethodsFingerprint.dexMethodList }.getOrNull().orEmpty()
        .forEach { dm -> runCatching { hookAdRequestNoOp(dm.toMethod()) } }

    // ── 5. Position-one feed ad ──────────────────────────────────────────────

    runCatching { ::newsFeedPosOneAdEligibilityFingerprint.dexMethodList }.getOrNull().orEmpty()
        .forEach { dm -> runCatching { hookForceBoolean(dm.toMethod(), false) } }

    // ── 6. Ad-channel network layer ──────────────────────────────────────────
    //
    // One level below section 1. That section stops the async-ad controller deciding to
    // request; this stops the request being put on the wire at all, which matters because
    // the news feed, Reels and the mid-session top-up each reach the network layer through
    // callers the controller does not own.

    runCatching { ::adChannelNetworkRequestMethodsFingerprint.dexMethodList }.getOrNull().orEmpty()
        .forEach { dm -> runCatching { hookAdRequestNoOp(dm.toMethod()) } }

    // ── 7. Video Home / Reels ad pipeline ────────────────────────────────────
    //
    // Section 4 covers one insertion point, for one ad kind. This covers the fetch, the
    // general insertion step, the delayed real-time-intent render and the mid-card ad
    // survey — the routes every other Reels ad takes.

    runCatching { ::videoHomeAdsPipelineMethodsFingerprint.dexMethodList }.getOrNull().orEmpty()
        .forEach { dm -> runCatching { hookAdRequestNoOp(dm.toMethod()) } }

    // ── 8. Stories viewer payload fetch ──────────────────────────────────────
    //
    // The second route the viewer has for topping up its ad buckets. Section 2 blocks the
    // plain fetch; this blocks the payload flavour, which is a separate method.

    runCatching { ::storyViewerAdsPayloadFetchMethodsFingerprint.dexMethodList }.getOrNull().orEmpty()
        .forEach { dm -> runCatching { hookAdRequestNoOp(dm.toMethod()) } }

    // ── 9. Search "AI mode" ad story query ───────────────────────────────────

    runCatching { ::searchAiModeAdStoryQueryFingerprint.dexMethodList }.getOrNull().orEmpty()
        .forEach { dm -> runCatching { hookAdRequestNoOp(dm.toMethod()) } }

    // ── 10. Lớp cơ sở của ad-channel controller ──────────────────────────────
    //
    // Mục 1 chặn FeedAsyncAdsController. Nhưng controller đó chỉ là MỘT trong hai lớp con của
    // một lớp cơ sở trừu tượng, và lớp con còn lại không ghi đè hai method này — nó chạy
    // thẳng code của lớp cơ sở, nên mục 1 không chạm tới. Hook ở đây đặt vào chính lớp cơ sở.
    // Xem [csrAdChannelRequestMethodsFingerprint] để biết cấu trúc thừa kế đó và vì sao lớp
    // con thứ hai không thể tìm ra bằng cách dò chuỗi.

    runCatching { ::csrAdChannelRequestMethodsFingerprint.dexMethodList }.getOrNull().orEmpty()
        .forEach { dm -> runCatching { hookAdRequestNoOp(dm.toMethod()) } }

    runCatching { ::csrAdChannelResultMethodsFingerprint.dexMethodList }.getOrNull().orEmpty()
        .forEach { dm -> runCatching { hookEmptyCollectionResult(dm.toMethod()) } }

    // ── 11. Subscriber nhận response của ad channel ──────────────────────────
    //
    // Sớm hơn mục 1 một bước: chặn ngay chỗ response đi vào, nên một request đã lên đường
    // trước khi module kịp cài hook cũng không còn được xử lý.

    runCatching { ::adsChannelSubscriberNextFingerprint.dexMethodList }.getOrNull().orEmpty()
        .forEach { dm -> runCatching { hookAdRequestNoOp(dm.toMethod()) } }

    runCatching { ::adsChannelSubscriberResultFingerprint.dexMethodList }.getOrNull().orEmpty()
        .forEach { dm -> runCatching { hookEmptyCollectionResult(dm.toMethod()) } }

    // ── 12. Hai method void phụ trợ ─────────────────────────────────────────
    //
    // Tham số dữ liệu quảng cáo trong request feed, và bước xếp lại thứ hạng quảng cáo trong
    // sponsored story holder. Cả hai đều đã được soi từng literal một — xem chú thích của
    // [feedAdRequestParamMethodsFingerprint] về method thứ ba trông giống hệt nhưng bị loại.

    runCatching { ::feedAdRequestParamMethodsFingerprint.dexMethodList }.getOrNull().orEmpty()
        .forEach { dm -> runCatching { hookAdRequestNoOp(dm.toMethod()) } }

    // ── 13. Trình phát toàn màn hình (short-form deep dive) ──────────────────
    //
    // Surface riêng, hàng đợi "TIẾP THEO" riêng, pipeline quảng cáo riêng — và trước đây
    // không có một dòng nào trong module chạm tới nó. Xem
    // [deepDiveAsyncAdRequestMethodsFingerprint] để biết nó được tìm ra thế nào.
    //
    // Hai kiểu trả về nên hai cách chặn: method void thì bỏ qua thân, còn lambda Kotlin
    // trả Object thì trả null — đúng cách mục 9 đã làm với query ads của Search AI mode,
    // vốn cũng là một lambda invoke(): Object.

    runCatching { ::deepDiveAsyncAdRequestMethodsFingerprint.dexMethodList }.getOrNull().orEmpty()
        .forEach { dm ->
            runCatching {
                val method = dm.toMethod()
                if (method.returnType == Void.TYPE) hookAdRequestNoOp(method) else hookAdQueryFetch(method)
            }
        }

    runCatching { ::deepDiveChainAdMethodsFingerprint.dexMethodList }.getOrNull().orEmpty()
        .forEach { dm -> runCatching { hookAdRequestNoOp(dm.toMethod()) } }
}
