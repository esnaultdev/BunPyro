package dev.esnault.bunpyro.data.network.entities

import com.squareup.moshi.Json
import dev.esnault.bunpyro.data.network.entities.user.LightUserInfo


/**
 * Base request for the non versioned Bunpro API.
 * Every request provides both an object with the query result, and another node with some info
 * about the user.
 */
data class BaseRequest<T>(
    @Json(name = "requested_information") val requestedInfo: T,
    @Json(name = "user_information") val userInfo: LightUserInfo
)
