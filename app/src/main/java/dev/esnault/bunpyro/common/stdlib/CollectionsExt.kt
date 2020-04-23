package dev.esnault.bunpyro.common.stdlib


/**
 * Returns a read-only [List] of all keys in this map.
 */
val <K, V> Map<K, V>.keysList: List<K>
    get() = keys.toList()
