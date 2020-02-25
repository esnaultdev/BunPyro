package dev.esnault.bunpyro.data.network.entities

import com.squareup.moshi.Json


/**
 * Base request for the non versioned Bunpro API.
 * Every request provides both an object with the query result, and another node with some info
 * about the user.
 */
class BaseRequest<T>(
    @Json(name = "requested_information") val requestedInfo: T
    // We don't need to parse the user_information object for each request, so let's just ignore it
)
