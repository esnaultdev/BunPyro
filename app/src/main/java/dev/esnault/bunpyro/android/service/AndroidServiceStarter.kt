package dev.esnault.bunpyro.android.service

import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat


class AndroidServiceStarter(
    private val context: Context
) : IAndroidServiceStarter {

    override fun startSync() {
        val intent = Intent(context, AndroidSyncService::class.java)
        ContextCompat.startForegroundService(context, intent)
    }
}
