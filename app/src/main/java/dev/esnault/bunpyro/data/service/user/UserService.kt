package dev.esnault.bunpyro.data.service.user

import dev.esnault.bunpyro.data.config.IAppConfig
import dev.esnault.bunpyro.data.utils.time.ITimeProvider
import dev.esnault.bunpyro.domain.DomainConfig
import dev.esnault.bunpyro.domain.entities.user.UserSubscription
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.first


class UserService(
    private val appConfig: IAppConfig,
    private val timeProvider: ITimeProvider
) : IUserService {

    private val serviceScope = CoroutineScope(SupervisorJob())

    init {
        initSubscription()
    }

    // region Subscription

    private val _subscription = MutableSharedFlow<UserSubscription>(replay = 1)
    override val subscription: Flow<UserSubscription>
        get() = _subscription.asSharedFlow()

    private var refreshSubscriptionJob: Deferred<UserSubscription?>? = null

    private fun initSubscription() {
        serviceScope.launch(Dispatchers.IO) {
            val value = appConfig.getSubscription()
            _subscription.emit(value)
        }
    }

    override suspend fun checkSubscription(): UserSubscription {
        val current = subscription.first()
        if (!current.shouldRefresh()) return current
        return refreshAndUpdateSubscription() ?: subscription.first()
    }

    override fun refreshSubscription(force: Boolean) {
        serviceScope.launch {
            if (force || subscription.first().shouldRefresh()) {
                refreshAndUpdateSubscription()
            }
        }
    }

    private fun UserSubscription.shouldRefresh(): Boolean {
        val lastCheck = lastCheck ?: return true

        // Always refresh for non subscribers to not lock them if they have subscribed since the
        // last check.
        if (!subscribed) return true

        val currentTimeMs = timeProvider.currentTimeMillis()
        return lastCheck.time + DomainConfig.SUBSCRIPTION_REFRESH_DELAY_MS < currentTimeMs
    }

    private suspend fun refreshAndUpdateSubscription(): UserSubscription? {
        // Reuse an active subscription check if possible
        val currentJob = refreshSubscriptionJob
        return if (currentJob?.isActive == true) {
            currentJob.await()
        } else {
            val newJob = serviceScope.async {
                val subscribed = false // TODO get this value from the network
                val now = timeProvider.currentDate()
                val newSubscription = UserSubscription(subscribed = subscribed, lastCheck = now)
                appConfig.setSubscription(newSubscription)
                newSubscription
            }
            refreshSubscriptionJob = newJob
            newJob.await()
        }
    }

    // endregion
}
