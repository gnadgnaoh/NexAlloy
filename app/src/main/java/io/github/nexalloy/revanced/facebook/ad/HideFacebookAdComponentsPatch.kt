package io.github.nexalloy.revanced.facebook.ad

import io.github.nexalloy.patch
import io.github.nexalloy.revanced.facebook.hookAdComponentRender
import io.github.nexalloy.revanced.facebook.hookAdQueryFetch

/**
 * Suppresses ad-only Litho components across every surface found in the app: Shorts and
 * Reels ad chrome, in-stream ad break cards, the Stories viewer, search results, Watch
 * immersive, multi-ads carousels, horizontal-scroll ad rails and the ads-in-comments
 * section.
 *
 * Kept as its own toggle rather than folded into [HideFacebookAds] for one reason: it is
 * by far the broadest rule in the module, roughly a hundred components, and its failure
 * mode is silent. A wrongly included component does not crash — the surface simply stops
 * drawing, which is easy to miss for weeks and hard to trace back afterwards. One switch
 * reverts all of it while the feed, plugin and descriptor layers keep working.
 *
 * The tag list is curated, not the raw output of a string scan. Three categories were
 * deliberately left out:
 *
 *  - **Playback controls.** Skip buttons, the ad break player, its view coordinator and
 *    control components, the post-hide countdown and the play indicator. Suppressing
 *    these removes the means of escaping an ad, or stalls the player waiting for a break
 *    that never finishes drawing. On this build three of them share one class, so
 *    hooking any single one would take out playback control for all three.
 *  - **False positives from substring matching.** "ads" hides inside Threads and Heads,
 *    and "interstitial" in this app also means login and zero-rating interstitials.
 *    Suppressing those breaks messaging and sign-in, not advertising.
 *  - **The Ad Activity screen.** A settings surface for reviewing ads you have seen, not
 *    an advertisement.
 */
/** Xem khối chú thích ở chỗ dùng, trong [HideFacebookAdComponents]. */
private const val HOOK_SEARCH_AI_MODE_ADS_QUERY = false

val HideFacebookAdComponents = patch(
    name = "Hide ad-only components",
    description = "Removes Litho components that exist purely to draw ads, across Reels, Shorts, Stories, Watch, search and in-stream video. Turn off if any video or feed surface stops rendering.",
) {
    runCatching { ::adSurfaceRenderMethodsFingerprint.dexMethodList }.getOrNull().orEmpty()
        .forEach { dm -> runCatching { hookAdComponentRender(dm.toMethod()) } }

    runCatching { ::adSectionRenderMethodsFingerprint.dexMethodList }.getOrNull().orEmpty()
        .forEach { dm -> runCatching { hookAdComponentRender(dm.toMethod()) } }

    // Stories ads. Resolved structurally, by the AdStory field a component carries,
    // rather than from a list of component names.
    runCatching { ::storyAdComponentRenderMethodsFingerprint.dexMethodList }.getOrNull().orEmpty()
        .forEach { dm -> runCatching { hookAdComponentRender(dm.toMethod()) } }

    // ── Search "AI mode" ads: TẮT trên bản FB 2026-08 ────────────────────────────
    //
    // Trên bản này [searchAiModeAdsQueryFingerprint] khớp đúng một method:
    //
    //     X.X9d#invoke() : java.lang.Object
    //
    // Một Function0 dùng chung — không tham số, trả Object. Hình dạng đó không cho biết nó
    // đang trả về CÁI GÌ, nên ép nó trả null là đẩy null vào tay một caller không xác định.
    // Đúng thứ mà ghi chú của [hookAdQueryFetch] đã cảnh báo từ đầu: "If a surface ever
    // hangs waiting on one of these, this is the hook to disable."
    //
    // Triệu chứng khi bật: đăng status và chia sẻ bài đều TREO — thanh tiến trình hiện ra
    // rồi đứng yên vô hạn. Phân biệt hai kiểu hỏng cho lần sau: một render bị nuốt thì
    // phần tử BIẾN MẤT; còn ĐỨNG YÊN nghĩa là có ai đó đang chờ một giá trị không bao giờ
    // tới, tức là lỗi ở tầng chặn request chứ không phải tầng component.
    //
    // Đánh đổi: mất phần chặn quảng cáo trong chế độ AI của ô tìm kiếm — một surface hẹp.
    //
    // Muốn bật lại ở bản FB sau: đổi [HOOK_SEARCH_AI_MODE_ADS_QUERY] thành true, RỒI kiểm
    // tra fingerprint khớp trúng method nào trước khi tin. Nếu nó vẫn ra một `invoke()`
    // không tham số trả Object thì câu trả lời vẫn là không.
    @Suppress("KotlinConstantConditions")
    if (HOOK_SEARCH_AI_MODE_ADS_QUERY) {
        runCatching { ::searchAiModeAdsQueryFingerprint.dexMethodList }.getOrNull().orEmpty()
            .forEach { dm -> runCatching { hookAdQueryFetch(dm.toMethod()) } }
    }

    // Stop requesting story ads in the first place, so none of the UI above is ever built.
    //
    // Nhóm này an toàn theo cách mà nhóm trên không có: ngoài chuỗi neo, nó còn ràng buộc
    // đúng bốn kiểu tham số, trong đó có FbUserSession. Nhờ vậy nó không thể với tới một
    // lambda dùng chung — đây là mẫu nên theo cho mọi hook ép trả null ở tầng request.
    runCatching { ::storiesAdsPaginationMethodFingerprint.dexMethodList }.getOrNull().orEmpty()
        .forEach { dm -> runCatching { hookAdQueryFetch(dm.toMethod()) } }
}
