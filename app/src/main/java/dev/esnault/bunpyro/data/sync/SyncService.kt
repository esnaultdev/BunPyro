package dev.esnault.bunpyro.data.sync

import dev.esnault.bunpyro.data.db.grammarpoint.GrammarPointDao
import dev.esnault.bunpyro.data.mapper.apitodb.GrammarPointMapper
import dev.esnault.bunpyro.data.network.BunproVersionedApi


class SyncService(
    private val versionedApi: BunproVersionedApi,
    private val grammarPointDao: GrammarPointDao
) : ISyncService {

    override suspend fun syncGrammar() {
        val grammarPoints = versionedApi.getGrammarPoints().data
        val mapper = GrammarPointMapper()
        grammarPointDao.insertAll(mapper.map(grammarPoints))
    }
}
