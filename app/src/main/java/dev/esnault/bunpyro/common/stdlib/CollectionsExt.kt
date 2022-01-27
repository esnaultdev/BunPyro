package dev.esnault.bunpyro.common.stdlib


/**
 * Returns a read-only [List] of all keys in this map.
 */
val <K, V> Map<K, V>.keysList: List<K>
    get() = keys.toList()

/**
 * Returns this [List] if all elements are not null, or null if any element is null.
 */
fun <T> List<T?>.takeIfAllNonNull(): List<T>? {
    val listOfNonNulls = filterNotNull()
    return if (listOfNonNulls.size == size) {
        listOfNonNulls
    } else {
        null
    }
}