package dev.esnault.bunpyro.data.service.migration


interface IMigrationService {

    suspend fun migrate()
}
