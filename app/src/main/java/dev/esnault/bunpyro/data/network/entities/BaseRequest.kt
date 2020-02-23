package dev.esnault.bunpyro.data.network.entities

import com.squareup.moshi.Json


class BaseRequest<T>(
    @Json(name = "requested_information") val requestedInfo: T
    // We don't need to parse the user_information object for each request, so let's just ignore it
)
