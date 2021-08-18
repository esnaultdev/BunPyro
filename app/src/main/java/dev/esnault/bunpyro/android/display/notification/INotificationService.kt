package dev.esnault.bunpyro.android.display.notification


interface INotificationService {

    // region Sync

    fun buildSyncNotification(): NotificationWithId
    fun hideSyncNotification()

    // endregion

    // region Reviews

    fun showReviewsNotification(count: Int)
    fun hideReviewsNotification()
    fun openReviewsNativeSettings()
    suspend fun isReviewsNotificationEnabled(): Boolean

    // endregion

}
