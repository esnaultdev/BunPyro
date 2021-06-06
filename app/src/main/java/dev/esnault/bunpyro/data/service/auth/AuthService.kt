package dev.esnault.bunpyro.data.service.auth

import dev.esnault.bunpyro.data.config.IAppConfig
import dev.esnault.bunpyro.data.repository.review.IReviewRepository
import dev.esnault.bunpyro.data.repository.settings.ISettingsRepository
import dev.esnault.bunpyro.data.service.user.IUserService
import dev.esnault.bunpyro.data.utils.crashreport.ICrashReporter
import dev.esnault.bunpyro.domain.entities.user.UserSubscription


class AuthService(
    private val reviewRepo: IReviewRepository,
    private val settingsRepo: ISettingsRepository,
    private val userService: IUserService,
    private val appConfig: IAppConfig,
    private val reporter: ICrashReporter
) : IAuthService {

    override suspend fun logout(): Boolean {
        return if (clearReviews()) {
            settingsRepo.clearAll()
            userService.cancelRefresh()
            clearUserDataInAppConfig()
            true
        } else {
            false
        }
    }

    private suspend fun clearReviews(): Boolean {
        return try {
            reviewRepo.clearAll()
            true
        } catch (e: Exception) {
            // If we can't clear the reviews, we can't proceed with the logout.
            // It would make the next user connecting still have the previous information.
            reporter.recordNonFatal(e)
            false
        }
    }

    private suspend fun clearUserDataInAppConfig() {
        appConfig.saveReviewsEtag(null)
        appConfig.saveFirstSyncCompleted(false)
        appConfig.setStudyQueueCount(null)
        appConfig.setUserName(null)
        appConfig.setApiKey(null)
        appConfig.setSubscription(UserSubscription.DEFAULT)
    }
}
