package dev.esnault.bunpyro.android.service

import android.app.IntentService
import android.content.Context
import android.content.Intent
import dev.esnault.bunpyro.android.display.notification.INotificationService
import dev.esnault.bunpyro.data.service.sync.ISyncService
import dev.esnault.bunpyro.data.service.sync.SyncType
import dev.esnault.bunpyro.data.utils.crashreport.ICrashReporter
import kotlinx.coroutines.runBlocking
import org.koin.android.ext.android.inject
import java.lang.Exception


/**
 * Android service used to sync the grammar content.
 */
class AndroidSyncService : IntentService("AndroidSyncService") {

    private val syncService: ISyncService by inject()
    private val notificationService: INotificationService by inject()
    private val crashReporter: ICrashReporter by inject()

    override fun onHandleIntent(intent: Intent?) {
        val notifWithId = notificationService.buildSyncNotification()
        startForeground(notifWithId.id, notifWithId.notification)

        val syncType = intent?.getStringExtra(EXTRA_TYPE).let(SyncType.Companion::fromValue)

        runBlocking {
            try {
                syncService.nextSync(syncType)
            } catch (e: Exception) {
                crashReporter.recordNonFatal(e)
            }
        }

        notificationService.hideSyncNotification()
    }

    companion object {
        private const val EXTRA_TYPE = "extra:type"

        fun createIntent(context: Context, type: SyncType): Intent {
            return Intent(context, AndroidSyncService::class.java)
                .putExtra(EXTRA_TYPE, type.value)
        }
    }
}
