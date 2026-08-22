package io.github.nexalloy.morphe.twitter.timeline.showpollresults

import app.morphe.extension.shared.Logger
import io.github.nexalloy.patch

val ShowPollResults = patch(
    name = "Show poll results",
    description = "Adds an option to show poll results without voting.",
) {
    val bindingValuesField = ::pollBindingValuesFieldResolved.field.also { it.isAccessible = true }

    JsonCardInstanceDataFingerprint.hookMethod {
        after { param ->
            val candidates = buildList {
                param.thisObject?.let { add(it) }
                param.args?.forEach { it?.let { arg -> add(arg) } }
            }

            for (candidate in candidates) {
                val map = runCatching {
                    @Suppress("UNCHECKED_CAST")
                    bindingValuesField.get(candidate) as? MutableMap<Any?, Any?>
                }.getOrNull() ?: continue

                runCatching { bindingValuesField.set(candidate, computePollResultsMap(map)) }
                break
            }
        }
    }
}

private fun computePollResultsMap(map: MutableMap<Any?, Any?>): Map<Any?, Any?> {
    try {
        val countsAreFinal = map["counts_are_final"]
        if (countsAreFinal != null && countsAreFinal.toString() == "true") {
            return map
        }

        val labels = listOf("choice1_label", "choice2_label", "choice3_label", "choice4_label")
        val counts = listOf("choice1_count", "choice2_count", "choice3_count", "choice4_count")

        var totalVotes = 0
        for (count in counts) {
            val value = map[count] ?: break
            totalVotes += value.toString().toInt()
        }
        if (totalVotes <= 0) return map

        val newMap = LinkedHashMap<Any?, Any?>()
        for ((key, value) in map) {
            val labelIndex = labels.indexOf(key?.toString())
            if (value != null && labelIndex >= 0) {
                val countLabel = counts[labelIndex]
                val count = map[countLabel]?.toString()?.toIntOrNull() ?: 0
                val percentage = Math.round(count * 100.0f / totalVotes)

                val replaced = runCatching {
                    value.javaClass
                        .getConstructor(Any::class.java, String::class.java)
                        .newInstance("$value - $percentage%", null)
                }.getOrNull()

                newMap[key] = replaced ?: value
                continue
            }
            newMap[key] = value
        }
        return newMap
    } catch (e: Throwable) {
        Logger.printException({ "[Twitter] ShowPollResults: failed to compute poll results" }, e)
        return map
    }
}
