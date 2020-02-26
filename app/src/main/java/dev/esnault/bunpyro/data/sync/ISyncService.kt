package dev.esnault.bunpyro.data.sync


interface ISyncService {

    suspend fun firstSync()
}
