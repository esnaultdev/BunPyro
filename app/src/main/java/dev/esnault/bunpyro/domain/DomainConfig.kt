package dev.esnault.bunpyro.domain

import java.util.concurrent.TimeUnit


object DomainConfig {

    const val STUDY_BURNED = 12

    /**
     * The minimal delay between two checks of the subscription status.
     */
    val SUBSCRIPTION_REFRESH_DELAY_MS = TimeUnit.HOURS.toMillis(20L)

    /**
     * The maximum duration a subscription is valid.
     */
    val SUBSCRIPTION_EXPIRATION_MS = TimeUnit.DAYS.toMillis(30L)
}
