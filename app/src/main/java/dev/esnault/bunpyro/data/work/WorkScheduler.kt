package dev.esnault.bunpyro.data.work

import androidx.work.*
import dev.esnault.bunpyro.BuildConfig
import dev.esnault.bunpyro.android.display.notification.INotificationService
import dev.esnault.bunpyro.data.repository.settings.ISettingsRepository
import java.util.concurrent.TimeUnit


class WorkScheduler(
    private val workManager: WorkManager,
    private val notificationService: INotificationService,
    private val settingsRepository: ISettingsRepository,
) : IWorkScheduler {

    private object WorkNames {
        const val REVIEW_COUNT = "ReviewCount"
    }

    // region Review count

    override suspend fun setupOrCancelReviewCountWork() {
        val notificationsEnabled = notificationService.isReviewsNotificationEnabled()
        if (notificationsEnabled) {
            enqueueReviewCountWork(keepExisting = true)
        } else {
            cancelReviewCountWork()
        }
    }

    override suspend fun rescheduleReviewCountWork() {
        val notificationsEnabled = notificationService.isReviewsNotificationEnabled()
        if (notificationsEnabled) {
            enqueueReviewCountWork(keepExisting = false)
        }
    }

    private suspend fun enqueueReviewCountWork(keepExisting: Boolean) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.UNMETERED)
            .setRequiresBatteryNotLow(true)
            .build()

        val intervalMinutes: Long = settingsRepository.getReviewsNotificationRefreshRateMinutes()

        val request = PeriodicWorkRequestBuilder<ReviewCountWorker>(
            /* repeatInterval */ intervalMinutes, TimeUnit.MINUTES,
            /* flexTimeInterval */ 5, TimeUnit.MINUTES
        )
            .setConstraints(constraints)
            .apply {
                // Don't apply an initial delay in debug, to be more convenient to debug.
                if (!BuildConfig.DEBUG) {
                    setInitialDelay(intervalMinutes, TimeUnit.MINUTES)
                }
            }
            .build()

        val existingWorkPolicy = if (keepExisting) {
            ExistingPeriodicWorkPolicy.KEEP
        } else {
            ExistingPeriodicWorkPolicy.REPLACE
        }
        workManager.enqueueUniquePeriodicWork(
            WorkNames.REVIEW_COUNT, existingWorkPolicy, request
        )
    }

    private fun cancelReviewCountWork() {
        workManager.cancelUniqueWork(WorkNames.REVIEW_COUNT)
    }

    // endregion
}
