package dev.esnault.bunpyro.data.network.entities

import com.squareup.moshi.Json


/**
 * Base data request of the Bunpro API.
 * Multiple requests wrap their result in a root "data" node if the result is an array.
 */
class DataRequest<T>(
    @Json(name = "data") val data: List<T>
)
