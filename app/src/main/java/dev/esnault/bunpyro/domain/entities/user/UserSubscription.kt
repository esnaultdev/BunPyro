package dev.esnault.bunpyro.domain.entities.user

import dev.esnault.bunpyro.domain.DomainConfig
import java.util.*


data class UserSubscription(
    val status: SubscriptionStatus,
    val lastCheck: Date?
) {

    fun expireIfNeeded(now: Date): UserSubscription {
        if (lastCheck == null || !status.isSubscribed) return this

        return if (now.time - lastCheck.time < DomainConfig.SUBSCRIPTION_EXPIRATION_MS) {
            this
        } else {
            UserSubscription(
                status = SubscriptionStatus.EXPIRED,
                lastCheck = lastCheck
            )
        }
    }

    companion object {
        val DEFAULT = UserSubscription(
            status = SubscriptionStatus.NOT_SUBSCRIBED,
            lastCheck = null
        )
    }
}

enum class SubscriptionStatus {
    /** The user is subscribed and the last check has been done recently enough. */
    SUBSCRIBED,

    /** The user is not subscribed. */
    NOT_SUBSCRIBED,

    /** The user was subscribed during the last check, but this check is too old. */
    EXPIRED;

    val isSubscribed: Boolean
        get() = this == SUBSCRIBED
}
