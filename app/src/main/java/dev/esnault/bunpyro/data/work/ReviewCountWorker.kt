package dev.esnault.bunpyro.data.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dev.esnault.bunpyro.android.display.notification.INotificationService
import dev.esnault.bunpyro.data.config.IAppConfig
import dev.esnault.bunpyro.data.repository.review.IReviewRepository
import dev.esnault.bunpyro.data.repository.settings.ISettingsRepository
import dev.esnault.bunpyro.domain.entities.user.StudyQueueStatus
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

        val oldStatus = appConfig.getStudyQueueCount()?.let { oldCount ->
            StudyQueueStatus(
                reviewCount = oldCount,
                nextReviewDate = appConfig.getNextReviewDate(),
            )
        }
        reviewRepository.refreshReviewStatus()
            .fold(
                onSuccess = { newStatus ->
                    onCountChange(oldStatus, newStatus)
                    Result.success()
                },
                onFailure = { Result.failure() }
            )
    }

    private suspend fun onCountChange(oldStatus: StudyQueueStatus?, newStatus: StudyQueueStatus) {
        val threshold = settingsRepository.getReviewsNotificationThreshold()
        if (newStatus.reviewCount < threshold) return

        // This can collide, but the API doesn't provide any way to differentiate the two cases.
        if (oldStatus == newStatus) return

        notificationService.showReviewsNotification(newStatus.reviewCount)
    }
}
