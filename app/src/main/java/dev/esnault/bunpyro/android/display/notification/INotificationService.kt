package dev.esnault.bunpyro.android.display.notification


interface INotificationService {
    fun buildSyncNotification(): NotificationWithId
    fun hideSyncNotification()
}
