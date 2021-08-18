package dev.esnault.bunpyro.data.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dev.esnault.bunpyro.android.display.notification.INotificationService
import dev.esnault.bunpyro.data.config.IAppConfig
import dev.esnault.bunpyro.data.repository.review.IReviewRepository
import dev.esnault.bunpyro.data.repository.settings.ISettingsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent


class ReviewCountWorker(
    context: Context,
    params: WorkerParameters,
    private val reviewRepository: IReviewRepository,
    private val settingsRepository: ISettingsRepository,
    private val appConfig: IAppConfig,
    private val notificationService: INotificationService
) : CoroutineWorker(context, params), KoinComponent {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
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

        // TODO Display the notification only if needed, and if not already reviewing.
        notificationService.showReviewsNotification(newCount)
    }
}
