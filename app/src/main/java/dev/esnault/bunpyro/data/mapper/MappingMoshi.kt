package dev.esnault.bunpyro.data.mapper

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dev.esnault.bunpyro.domain.utils.lazyNone

/**
 * Moshi instance used to map JSON values when mapping
 * This should probably be part of the dependency injection.
 */
val manualMappingMoshi: Moshi by lazyNone {
    Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()
}
