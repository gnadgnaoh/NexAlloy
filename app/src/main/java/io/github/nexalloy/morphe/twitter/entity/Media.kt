package io.github.nexalloy.morphe.twitter.entity

data class Media(val type: Int, val url: String, val ext: String) {
    companion object {
        const val TYPE_IMAGE = 0
        const val TYPE_VIDEO = 1
    }
}
