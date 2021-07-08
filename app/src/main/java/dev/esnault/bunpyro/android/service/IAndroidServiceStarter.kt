package dev.esnault.bunpyro.android.service

import dev.esnault.bunpyro.data.service.sync.SyncType


interface IAndroidServiceStarter {
    fun startSync(type: SyncType)
}
