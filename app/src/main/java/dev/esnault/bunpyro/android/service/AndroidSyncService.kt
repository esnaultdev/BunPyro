package dev.esnault.bunpyro.android.service

import android.app.IntentService
import android.content.Intent
import dev.esnault.bunpyro.android.display.notification.INotificationService
import dev.esnault.bunpyro.data.sync.ISyncService
import kotlinx.coroutines.runBlocking
import org.koin.android.ext.android.inject


/**
 * Android service used to sync the grammar content.
 */
class AndroidSyncService : IntentService("AndroidSyncService") {

    private val syncService: ISyncService by inject()
    private val notificationService: INotificationService by inject()

    override fun onHandleIntent(intent: Intent?) {
        val notifWithId = notificationService.buildSyncNotification()
        startForeground(notifWithId.id, notifWithId.notification)

        runBlocking {
            syncService.nextSync()
        }

        notificationService.hideSyncNotification()
    }
}
