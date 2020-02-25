package dev.esnault.bunpyro.data.sync

import android.util.Log
import dev.esnault.bunpyro.data.network.BunproVersionedApi


class SyncService(private val versionedApi: BunproVersionedApi) : ISyncService {

    override suspend fun syncGrammar() {
        val grammarPoints = versionedApi.getGrammarPoints().data

        // TODO Insert the grammar points in the local DB
        Log.d("SyncService", "${grammarPoints.size}")
    }
}
