package dev.esnault.bunpyro.data.service.migration

import dev.esnault.bunpyro.BuildConfig
import dev.esnault.bunpyro.data.config.IAppConfig
import dev.esnault.bunpyro.data.db.BunPyroDatabase
import org.koin.java.KoinJavaComponent.inject


class MigrationService(
    private val appConfig: IAppConfig
) : IMigrationService {

    private val bunpyroDb: BunPyroDatabase by inject(BunPyroDatabase::class.java)

    override suspend fun migrate() {
        // Warm up the DB, performing all DB migrations
        bunpyroDb.isOpen

        val fromVersionCode = appConfig.getMigrationVersionCode() ?: 0
        migrate(fromVersionCode)
        val newVersionCode = BuildConfig.VERSION_CODE
        appConfig.saveMigrationVersionCode(newVersionCode)
    }

    @Suppress("UNUSED_CHANGED_VALUE")
    private suspend fun migrate(fromVersionCode: Int) {
        var versionCode = fromVersionCode

        // The index of the history of all reviews was wrongly parsed before app version 21.
        // We can't recover from this by just transforming the data stored in DB, so we need to
        // delete it and sync it again.
        if (versionCode < 22) {
            if (appConfig.getReviewsEtag() != null) {
                appConfig.saveReviewsEtag(null)
                bunpyroDb.reviewHistoryDao().deleteAll()
                appConfig.saveFirstSyncCompleted(false)
            }
            versionCode = 22
        }
    }
}
