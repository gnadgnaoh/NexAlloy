package io.github.nexalloy.morphe.twitter.link.unshorten

import io.github.nexalloy.getObjectFieldOrNull
import io.github.nexalloy.setObjectField

internal fun unshortJsonUrlEntity(entity: Any?): Any? {
    if (entity == null) return entity
    runCatching {
        val expandedUrl = entity.getObjectFieldOrNull("c")
        entity.setObjectField("e", expandedUrl)
    }
    return entity
}
