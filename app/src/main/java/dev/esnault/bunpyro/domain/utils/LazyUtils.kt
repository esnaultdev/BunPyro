@file:Suppress("unused")

package dev.esnault.bunpyro.domain.utils

/**
 * Creates a new instance of the [Lazy] that uses the specified initialization function
 * [initializer] and the thread-safety mode [LazyThreadSafetyMode.NONE].
 *
 * See [lazy].
 */
fun <T> lazyNone(initializer: () -> T): Lazy<T> =
    lazy(LazyThreadSafetyMode.NONE, initializer)

/**
 * Creates a new instance of the [Lazy] that uses the specified initialization function
 * [initializer] and the thread-safety mode [LazyThreadSafetyMode.PUBLICATION].
 *
 * See [lazy].
 */
fun <T> lazyPublication(initializer: () -> T): Lazy<T> =
    lazy(LazyThreadSafetyMode.PUBLICATION, initializer)

/**
 * Creates a new instance of the [Lazy] that uses the specified initialization function
 * [initializer] and the thread-safety mode [LazyThreadSafetyMode.SYNCHRONIZED].
 *
 * See [lazy].
 */
fun <T> lazySynchronized(initializer: () -> T): Lazy<T> =
    lazy(LazyThreadSafetyMode.SYNCHRONIZED, initializer)
