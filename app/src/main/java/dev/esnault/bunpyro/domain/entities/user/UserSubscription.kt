package dev.esnault.bunpyro.domain.entities.user

import java.util.*


data class UserSubscription(
    val subscribed: Boolean,
    val lastCheck: Date?
) {
    companion object {
        val DEFAULT = UserSubscription(
            subscribed = false,
            lastCheck = null
        )
    }
}
