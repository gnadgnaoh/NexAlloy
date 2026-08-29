package io.github.nexalloy.morphe.twitter.link.unshorten

import de.robv.android.xposed.XC_MethodHook.MethodHookParam

internal fun unshortenArgs(
    param: MethodHookParam,
    displayIdx: Int,
    expandedIdx: Int,
    urlIdx: Int,
) {
    val expanded = param.args.getOrNull(expandedIdx) as? String ?: return
    if (expanded.isEmpty()) return
    param.args[displayIdx] = expanded
    param.args[urlIdx] = expanded
}
