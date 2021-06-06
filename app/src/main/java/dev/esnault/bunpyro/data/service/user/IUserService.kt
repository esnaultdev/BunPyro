package dev.esnault.bunpyro.data.service.user

import dev.esnault.bunpyro.domain.DomainConfig
import dev.esnault.bunpyro.domain.entities.user.UserSubscription
import kotlinx.coroutines.flow.Flow


interface IUserService {

    // region Subscription

    /**
     * A flow with the latest subscription status.
     */
    val subscription: Flow<UserSubscription>

    /**
     * Blocking call that tries to refresh the user subscription and return it.
     * If the refresh fails, the last known subscription status is returned instead.
     */
    suspend fun checkSubscription(): UserSubscription

    /**
     * Starts a subscription refresh in the background.
     * The actual refresh is only done after [DomainConfig.SUBSCRIPTION_REFRESH_DELAY_MS] since the
     * last check unless [force] is set to `true`.
     */
    fun refreshSubscription(force: Boolean = false)

    /**
     * Cancels a subscription refresh, if any.
     */
    suspend fun cancelRefresh()

    // endregion
}
