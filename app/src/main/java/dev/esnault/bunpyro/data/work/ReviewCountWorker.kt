package dev.esnault.bunpyro.data.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dev.esnault.bunpyro.android.display.notification.INotificationService
import dev.esnault.bunpyro.data.config.IAppConfig
import dev.esnault.bunpyro.data.repository.review.IReviewRepository
import dev.esnault.bunpyro.data.repository.settings.ISettingsRepository
import dev.esnault.bunpyro.domain.service.review.IReviewSessionService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent


class ReviewCountWorker(
    context: Context,
    params: WorkerParameters,
    private val reviewRepository: IReviewRepository,
    private val settingsRepository: ISettingsRepository,
    private val appConfig: IAppConfig,
    private val notificationService: INotificationService,
    private val workScheduler: IWorkScheduler,
    private val reviewSessionService: IReviewSessionService,
) : CoroutineWorker(context, params), KoinComponent {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        if (appConfig.getApiKey() == null) {
            workScheduler.cancelReviewCountWork()
            return@withContext Result.failure()
        }

        if (reviewSessionService.sessionInProgress) {
            return@withContext Result.success()
        }

        val oldCount = appConfig.getStudyQueueCount()
        reviewRepository.refreshReviewCount()
            .fold(
                onSuccess = { newCount ->
                    onCountChange(oldCount, newCount)
                    Result.success()
                },
                onFailure = { Result.failure() }
            )
    }

    private suspend fun onCountChange(oldCount: Int?, newCount: Int) {
        val threshold = settingsRepository.getReviewsNotificationThreshold()
        if (newCount < threshold) return

        // This can collide, but the API doesn't provide any way to differentiate the two cases.
        if (newCount == oldCount) return

        notificationService.showReviewsNotification(newCount)
    }
}
