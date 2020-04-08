package dev.esnault.bunpyro.common.stdlib


/**
 * Finds all occurrences of the specified strings in this char sequence,
 * starting from the specified startIndex and optionally ignoring the case.
 */
fun CharSequence.findAllOf(strings: Collection<String>, startIndex: Int, ignoreCase: Boolean = false): List<Pair<Int, String>> {
    if (strings.isEmpty()) return emptyList()

    // We don't need to search the last indices when the size of our strings won't fit
    val minLength = strings.map { it.length }.min()!!
    val lastSearchIndex = length - (minLength - 1).coerceAtLeast(0)
    val indices = startIndex.coerceAtLeast(0)..lastSearchIndex

    val result = mutableListOf<Pair<Int, String>>()

    if (this is String) {
        for (index in indices) {
            strings.filter { it.regionMatches(0, this, index, it.length, ignoreCase) }
                .forEach { result.add(index to it) }
        }
    } else {
        for (index in indices) {
            strings.filter { it.regionMatchesImpl(0, this, index, it.length, ignoreCase) }
                .forEach { result.add(index to it) }
        }
    }

    return result
}

/**
 * Direct copy from the stdlib since it's internal.
 *
 * Implementation of [regionMatches] for CharSequences.
 * Invoked when it's already known that arguments are not Strings, so that no additional type checks are performed.
 */
internal fun CharSequence.regionMatchesImpl(thisOffset: Int, other: CharSequence, otherOffset: Int, length: Int, ignoreCase: Boolean): Boolean {
    if ((otherOffset < 0) || (thisOffset < 0) || (thisOffset > this.length - length) || (otherOffset > other.length - length)) {
        return false
    }

    for (index in 0 until length) {
        if (!this[thisOffset + index].equals(other[otherOffset + index], ignoreCase))
            return false
    }
    return true
}
