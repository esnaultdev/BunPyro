package dev.esnault.bunpyro.data.config

import android.content.SharedPreferences
import androidx.core.content.edit
import dev.esnault.bunpyro.common.getIntOrNull
import dev.esnault.bunpyro.common.putOrRemoveInt
import dev.esnault.bunpyro.common.putOrRemoveString
import dev.esnault.bunpyro.data.mapper.settings.UserSubscriptionFromStringMapper
import dev.esnault.bunpyro.data.mapper.settings.UserSubscriptionToStringMapper
import dev.esnault.bunpyro.domain.entities.user.UserSubscription


private object Keys {
    // Migration
    const val MIGRATION_VERSION_CODE = "MigrationVersionCode"

    // Sync
    const val FIRST_SYNC_COMPLETED = "FirstSyncCompleted"
    const val SYNC_EXAMPLE_SENTENCES_ETAG = "ExampleSentencesEtag"
    const val SYNC_GRAMMAR_POINTS_ETAG = "SyncGrammarPointsEtag"
    const val SYNC_REVIEWS_ETAG = "ReviewsEtag"
    const val SYNC_SUPPLEMENTAL_LINKS_ETAG = "SupplementalLinksEtag"

    // User
    const val API_KEY = "ApiKey"
    const val STUDY_QUEUE_COUNT = "study_queue_count" // how come I messed this up?
    const val USER_NAME = "UserName"
    const val USER_SUBSCRIPTION = "UserSubscription"
}


class AppConfig(private val prefs: SharedPreferences) : IAppConfig {

    // region Migration

    override suspend fun getMigrationVersionCode(): Int? {
        return prefs.getIntOrNull(Keys.MIGRATION_VERSION_CODE)
    }

    override suspend fun saveMigrationVersionCode(versionCode: Int) {
        prefs.edit { putInt(Keys.MIGRATION_VERSION_CODE, versionCode) }
    }

    // endregion

    // region Sync

    override suspend fun getFirstSyncCompleted(): Boolean {
        return prefs.getBoolean(Keys.FIRST_SYNC_COMPLETED, false)
    }

    override suspend fun saveFirstSyncCompleted(completed: Boolean) {
        prefs.edit {
            putBoolean(Keys.FIRST_SYNC_COMPLETED, completed)
        }
    }

    override suspend fun getGrammarPointsEtag(): String? {
        return prefs.getString(Keys.SYNC_GRAMMAR_POINTS_ETAG, null)
    }

    override suspend fun saveGrammarPointsEtag(eTag: String?) {
        prefs.edit {
            putOrRemoveString(Keys.SYNC_GRAMMAR_POINTS_ETAG, eTag)
        }
    }

    override suspend fun getExampleSentencesEtag(): String? {
        return prefs.getString(Keys.SYNC_EXAMPLE_SENTENCES_ETAG, null)
    }

    override suspend fun saveExampleSentencesEtag(eTag: String?) {
        prefs.edit {
            putOrRemoveString(Keys.SYNC_EXAMPLE_SENTENCES_ETAG, eTag)
        }
    }

    override suspend fun getReviewsEtag(): String? {
        return prefs.getString(Keys.SYNC_REVIEWS_ETAG, null)
    }

    override suspend fun saveReviewsEtag(eTag: String?) {
        prefs.edit {
            putOrRemoveString(Keys.SYNC_REVIEWS_ETAG, eTag)
        }
    }

    override suspend fun getSupplementalLinksEtag(): String? {
        return prefs.getString(Keys.SYNC_SUPPLEMENTAL_LINKS_ETAG, null)
    }

    override suspend fun saveSupplementalLinksEtag(eTag: String?) {
        prefs.edit {
            putOrRemoveString(Keys.SYNC_SUPPLEMENTAL_LINKS_ETAG, eTag)
        }
    }

    // endregion

    // region User

    override suspend fun getApiKey(): String? {
        return prefs.getString(Keys.API_KEY, null)
    }

    override suspend fun setApiKey(apiKey: String?) {
        prefs.edit {
            putOrRemoveString(Keys.API_KEY, apiKey)
        }
    }

    override suspend fun setStudyQueueCount(count: Int?) {
        prefs.edit {
            putOrRemoveInt(Keys.STUDY_QUEUE_COUNT, count)
        }
    }

    override suspend fun getStudyQueueCount(): Int? {
        return prefs.getIntOrNull(Keys.STUDY_QUEUE_COUNT)
    }

    override suspend fun getUserName(): String? {
        return prefs.getString(Keys.USER_NAME, null)
    }

    override suspend fun setUserName(name: String?) {
        prefs.edit {
            putOrRemoveString(Keys.USER_NAME, name)
        }
    }

    override suspend fun getSubscription(): UserSubscription {
        val value = prefs.getString(Keys.USER_SUBSCRIPTION, null)
        return UserSubscriptionFromStringMapper().map(value)
    }

    override suspend fun setSubscription(subscription: UserSubscription) {
        val value = UserSubscriptionToStringMapper().map(subscription)
        prefs.edit { putString(Keys.USER_SUBSCRIPTION, value) }
    }

    // endregion
}
