package io.github.nexalloy.morphe.twitter.entity

import app.morphe.extension.shared.Logger
import io.github.nexalloy.patch
import java.lang.reflect.Field
import java.lang.reflect.Method

internal lateinit var tweetUsernameMethod: Method
internal lateinit var tweetProfileNameMethod: Method
internal lateinit var tweetUserIdMethod: Method
internal lateinit var tweetMediaMethod: Method
internal lateinit var extMediaListField: Field
internal lateinit var tweetNoteTweetMethod: Method
internal lateinit var longTextField: Field
internal lateinit var tweetShortTextMethod: Method
internal lateinit var tweetLangField: Field
internal lateinit var tweetInfoField: Field
internal lateinit var extMediaVideoInfoField: Field
internal lateinit var extMediaVideoVariantsField: Field
internal lateinit var extMediaImageField: Field

val EntityPatch = patch(name = "<EntityPatch>") {
    val failures = mutableListOf<String>()

    fun <T> resolve(stepName: String, assign: (T) -> Unit, resolver: () -> T) {
        try {
            assign(resolver())
        } catch (e: Throwable) {
            Logger.printException({ "[Twitter] EntityPatch: failed to resolve '$stepName'" }, e)
            failures += stepName
        }
    }

    resolve("tweetUsernameMethod", { tweetUsernameMethod = it }) {
        ::tweetUsernameMethodResolved.method.also { it.isAccessible = true }
    }
    resolve("tweetProfileNameMethod", { tweetProfileNameMethod = it }) {
        ::tweetProfileNameMethodResolved.method.also { it.isAccessible = true }
    }
    resolve("tweetUserIdMethod", { tweetUserIdMethod = it }) {
        ::tweetUserIdMethodResolved.method.also { it.isAccessible = true }
    }
    resolve("tweetMediaMethod", { tweetMediaMethod = it }) {
        ::tweetMediaMethodResolved.method.also { it.isAccessible = true }
    }
    resolve("extMediaListField", { extMediaListField = it }) {
        ::extMediaListFieldResolved.field.also { it.isAccessible = true }
    }
    resolve("tweetNoteTweetMethod", { tweetNoteTweetMethod = it }) {
        ::tweetNoteTweetMethodResolved.method.also { it.isAccessible = true }
    }
    resolve("longTextField", { longTextField = it }) {
        ::longTextFieldResolved.field.also { it.isAccessible = true }
    }
    resolve("tweetShortTextMethod", { tweetShortTextMethod = it }) {
        ::tweetShortTextMethodResolved.method.also { it.isAccessible = true }
    }
    resolve("tweetLangField", { tweetLangField = it }) {
        ::tweetLangFieldResolved.field.also { it.isAccessible = true }
    }
    resolve("tweetInfoField", { tweetInfoField = it }) {
        ::tweetInfoFieldResolved.field.also { it.isAccessible = true }
    }
    resolve("extMediaVideoInfoField", { extMediaVideoInfoField = it }) {
        ::extMediaVideoInfoFieldResolved.field.also { it.isAccessible = true }
    }
    resolve("extMediaVideoVariantsField", { extMediaVideoVariantsField = it }) {
        ::extMediaVideoVariantsFieldResolved.field.also { it.isAccessible = true }
    }
    resolve("extMediaImageField", { extMediaImageField = it }) {
        ::extMediaImageFieldResolved.field.also { it.isAccessible = true }
    }

    if (failures.isNotEmpty()) {
        throw Exception("EntityPatch: ${failures.size}/13 lookups failed: ${failures.joinToString()}")
    }
}
