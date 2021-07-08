package dev.esnault.bunpyro.android.service

import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import dev.esnault.bunpyro.data.service.sync.SyncType


class AndroidServiceStarter(
    private val context: Context
) : IAndroidServiceStarter {

    override fun startSync(type: SyncType) {
        val intent = AndroidSyncService.createIntent(context, type)
        ContextCompat.startForegroundService(context, intent)
    }
}
