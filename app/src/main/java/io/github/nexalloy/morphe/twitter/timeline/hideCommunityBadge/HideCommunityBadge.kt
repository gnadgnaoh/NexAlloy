package io.github.nexalloy.morphe.twitter.timeline.hideCommunityBadge

import io.github.nexalloy.patch

val HideCommunityBadge = patch(
    name = "Hide community badges",
    description = "Hides the community badge shown next to a user's name.",
) {
    CommModelFingerprint.hookMethod {
        after { param ->
            val instance = param.thisObject ?: return@after
            runCatching {
                val enumField = instance.javaClass.declaredFields.firstOrNull { it.type.isEnum }
                    ?: return@after
                enumField.isAccessible = true
                val nonMember = enumField.type.enumConstants
                    ?.firstOrNull { it.toString() == "NON_MEMBER" }
                    ?: return@after
                enumField.set(instance, nonMember)
            }
        }
    }
}
