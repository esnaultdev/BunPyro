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
import timber.log.Timber


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
        Timber.d("Starting work")

        if (appConfig.getApiKey() == null) {
            workScheduler.cancelReviewCountWork()
            Timber.d("Failure: missing API key")
            return@withContext Result.failure()
        }

        if (reviewSessionService.sessionInProgress) {
            Timber.d("Skipped: Review session in progress")
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
                onFailure = {
                    Timber.w(it, "Failure: API request error")
                    Result.failure()
                }
            )
    }

    private suspend fun onCountChange(oldStatus: StudyQueueStatus?, newStatus: StudyQueueStatus) {
        val threshold = settingsRepository.getReviewsNotificationThreshold()
        val reviewCount = newStatus.reviewCount
        if (reviewCount < threshold) {
            Timber.d("Skipped: Not enough reviews (reviews: $reviewCount, threshold: $threshold")
            return
        }

        // This can collide, but the API doesn't provide any way to differentiate the two cases.
        if (oldStatus == newStatus) {
            Timber.d("Skipped: Same review count as before $newStatus")
            return
        }

        Timber.d("Showing notification for $reviewCount review(s)")
        notificationService.showReviewsNotification(reviewCount)
    }
}
