package dev.esnault.bunpyro.data.network.entities.user

import com.squareup.moshi.Json


/**
 * The information about the user, with more details than [LightUserInfo].
 *
 * Note that most of the fields are skipped, refer to the official API doc for more details.
 */
data class FullUserInfo(
    val id: Long,
    @Json(name="username") val userName: String,
    val subscriber: Boolean
)
