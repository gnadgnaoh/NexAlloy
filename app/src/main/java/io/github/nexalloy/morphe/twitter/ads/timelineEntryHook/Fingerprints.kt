package io.github.nexalloy.morphe.twitter.ads.timelineEntryHook

import io.github.nexalloy.morphe.Fingerprint

internal object TimelineEntryHookFingerprint : Fingerprint(
    definingClass = "Lcom/twitter/model/json/timeline/urt/JsonTimelineEntry\$\$JsonObjectMapper;",
    name = "parse",
    returnType = "Ljava/lang/Object;",
)

internal object TimelineModuleItemHookFingerprint : Fingerprint(
    definingClass = "Lcom/twitter/model/json/timeline/urt/JsonTimelineModuleItem\$\$JsonObjectMapper;",
    name = "parse",
    returnType = "Ljava/lang/Object;",
)

internal object HidePromotedTrendFingerprint : Fingerprint(
    definingClass = "Lcom/twitter/model/json/timeline/urt/JsonTimelineTrend;",
    returnType = "Ljava/lang/Object;",
)
