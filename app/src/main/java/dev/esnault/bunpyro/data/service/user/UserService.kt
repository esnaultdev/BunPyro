package dev.esnault.bunpyro.data.service.user

import dev.esnault.bunpyro.data.config.IAppConfig
import dev.esnault.bunpyro.data.network.BunproVersionedApi
import dev.esnault.bunpyro.data.network.simpleRequest
import dev.esnault.bunpyro.data.repository.settings.ISettingsRepository
import dev.esnault.bunpyro.data.utils.crashreport.ICrashReporter
import dev.esnault.bunpyro.data.utils.time.ITimeProvider
import dev.esnault.bunpyro.domain.DomainConfig
import dev.esnault.bunpyro.domain.entities.settings.MockSubscriptionSetting
import dev.esnault.bunpyro.domain.entities.user.SubscriptionStatus
import dev.esnault.bunpyro.domain.entities.user.UserSubscription
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import timber.log.Timber
import java.util.*


class UserService(
    private val appConfig: IAppConfig,
    private val settingsRepo: ISettingsRepository,
    private val timeProvider: ITimeProvider,
    private val versionedApi: BunproVersionedApi,
    private val crashReporter: ICrashReporter
) : IUserService {

    private val serviceScope = CoroutineScope(SupervisorJob())

    private val _subscription = MutableSharedFlow<UserSubscription>(replay = 1)
    override val subscription: Flow<UserSubscription> = _subscription.asSharedFlow()
        .combine(settingsRepo.mockSubscription) { subscription, mockSetting ->
            mockSubscription(subscription, mockSetting)
        }

    private var refreshSubscriptionJob: Deferred<UserSubscription?>? = null

    init {
        initSubscription()
    }

    // region Subscription

    private fun initSubscription() {
        serviceScope.launch(Dispatchers.IO) {
            val value = appConfig.getSubscription().expireIfNeeded(timeProvider.currentDate())
            _subscription.emit(value)
        }
    }

    override suspend fun checkSubscription(): UserSubscription {
        val current = subscription.first()
        if (!current.shouldRefresh()) return current
        refreshAndUpdateSubscription()
        return subscription.first()
    }

    override fun refreshSubscription(force: Boolean) {
        serviceScope.launch {
            if (force || subscription.first().shouldRefresh()) {
                refreshAndUpdateSubscription()
            }
        }
    }

    override suspend fun cancelRefresh() {
        refreshSubscriptionJob?.cancelAndJoin()
    }

    private fun UserSubscription.shouldRefresh(): Boolean {
        val lastCheck = lastCheck ?: return true

        // Always refresh for non subscribers to not lock them if they have subscribed since the
        // last check.
        if (status != SubscriptionStatus.SUBSCRIBED) return true

        val currentTimeMs = timeProvider.currentTimeMillis()
        return lastCheck.time + DomainConfig.SUBSCRIPTION_REFRESH_DELAY_MS < currentTimeMs
    }

    private suspend fun refreshAndUpdateSubscription() {
        // Reuse an active subscription check if possible
        val currentJob = refreshSubscriptionJob
        try {
            if (currentJob?.isActive == true) {
                currentJob.await()
            } else {
                val newJob = serviceScope.async {
                    val subscribed = getSubscriptionFromNetwork()
                    if (subscribed != null) {
                        val newSubscription = UserSubscription(
                            status = if (subscribed) {
                                SubscriptionStatus.SUBSCRIBED
                            } else {
                                SubscriptionStatus.NOT_SUBSCRIBED
                            },
                            lastCheck = timeProvider.currentDate()
                        )
                        appConfig.setSubscription(newSubscription)
                        _subscription.emit(newSubscription)
                        newSubscription
                    } else {
                        null
                    }
                }
                refreshSubscriptionJob = newJob
                newJob.await()
            }
        } catch (e: CancellationException) {
            Timber.w("Subscription refresh was cancelled")
        }
    }

    private suspend fun getSubscriptionFromNetwork(): Boolean? {
        return simpleRequest(
            request = versionedApi::getUser,
            onSuccess = { response -> response.body()?.subscriber },
            onInvalidApiKey = {
                // TODO disconnect the user, clear the DB and redirect to the api key screen
                null
            },
            onServerError = { _, error ->
                crashReporter.recordNonFatal(error)
                null
            },
            onNetworkError = { null },
            onUnknownError = { error ->
                crashReporter.recordNonFatal(error)
                null
            }
        )
    }

    private fun mockSubscription(
        subscription: UserSubscription,
        mockSetting: MockSubscriptionSetting
    ): UserSubscription {
        return when (mockSetting) {
            MockSubscriptionSetting.ACTUAL -> subscription
            MockSubscriptionSetting.SUBSCRIBED -> UserSubscription(
                status = SubscriptionStatus.SUBSCRIBED,
                lastCheck = timeProvider.currentDate()
            )
            MockSubscriptionSetting.NOT_SUBSCRIBED -> UserSubscription(
                status = SubscriptionStatus.NOT_SUBSCRIBED,
                lastCheck = timeProvider.currentDate()
            )
            MockSubscriptionSetting.EXPIRED -> {
                val currentMs = timeProvider.currentTimeMillis()
                val lastCheck = Date(currentMs - DomainConfig.SUBSCRIPTION_EXPIRATION_MS)
                UserSubscription(
                    status = SubscriptionStatus.EXPIRED,
                    lastCheck = lastCheck
                )
            }
        }
    }

    // endregion
}
