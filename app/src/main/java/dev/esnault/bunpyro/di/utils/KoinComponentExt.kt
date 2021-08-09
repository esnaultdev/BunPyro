package dev.esnault.bunpyro.di.utils

import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

/**
 * Returns an instance of [T] from Koin.
 * This is an anti-pattern, prefer constructor injection when possible.
 */
inline fun <reified T> getKoinInstance(): T {
    return object : KoinComponent {
        val value: T by inject()
    }.value
}
