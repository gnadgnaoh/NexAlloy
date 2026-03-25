package io.github.nexalloy.morphe

import org.luckypray.dexkit.query.enums.StringMatchType

enum class StringComparisonType(val value: StringMatchType) {
    EQUALS(StringMatchType.Equals),
    CONTAINS(StringMatchType.Contains),
    STARTS_WITH(StringMatchType.StartsWith),
    ENDS_WITH(
        StringMatchType.EndsWith
    );

    /**
     * @param targetString The target string to search
     * @param searchString To search for in the target string (or to compare entirely for equality).
     */
    fun compare(targetString: CharSequence, searchString: CharSequence): Boolean {
        return when (this) {
            EQUALS -> targetString == searchString
            CONTAINS -> targetString.contains(searchString)
            STARTS_WITH -> targetString.startsWith(searchString)
            ENDS_WITH -> targetString.endsWith(searchString)
        }
    }

    internal companion object {
        internal fun typeDeclarationToComparison(type: Iterable<CharSequence>?): List<StringComparisonType> {
            return type?.map(::typeDeclarationToComparison).orEmpty()
        }

        fun typeDeclarationToComparison(type: CharSequence?): StringComparisonType {
            if (type == null) return EQUALS
            require(type.isNotEmpty()) {
                "type cannot be empty"
            }

            val firstChar = type[0]

            // First handle single character declarations.
            if (type.length == 1) {
                return when (firstChar) {
                    'B', 'C', 'D', 'F', 'I', 'J', 'S', 'V', 'Z' -> EQUALS
                    'L' -> STARTS_WITH
                    '[' -> STARTS_WITH
                    else -> throw IllegalArgumentException("Unknown type declaration: $type")
                }
            }

            val endsWithSemicolon = type.endsWith(';')

            if (firstChar == '[') {
                return if (endsWithSemicolon) EQUALS else STARTS_WITH
            }

            val startsWithL = (firstChar == 'L')

            when {
                startsWithL && endsWithSemicolon -> return EQUALS
                startsWithL -> return STARTS_WITH
                endsWithSemicolon -> return ENDS_WITH
            }

            return CONTAINS
        }
    }
}

internal fun parametersMatch(
    targetMethodParameters: Iterable<CharSequence>,
    fingerprintParameters: Iterable<CharSequence>,
    stringComparisonType: Iterable<StringComparisonType>
): Boolean {
    if (targetMethodParameters.count() != fingerprintParameters.count()) return false
    val fingerprintIterator = fingerprintParameters.iterator()
    val comparisonIterator = stringComparisonType.iterator()

    targetMethodParameters.forEach { targetParameter ->
        val comparison = comparisonIterator.next()
        val fingerprintParameter = fingerprintIterator.next()
        if (!comparison.compare(targetParameter, fingerprintParameter)) return false
    }

    return true
}
