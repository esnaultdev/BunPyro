package dev.esnault.bunpyro.data.config

import android.content.SharedPreferences
import androidx.core.content.edit

private object Keys {
    const val API_KEY = "ApiKey"
    const val SYNC_GRAMMAR_POINTS_ETAG = "SyncGrammarPointsEtag"
    const val FIRST_SYNC_COMPLETED = "FirstSyncCompleted"
}


class AppConfig(private val prefs: SharedPreferences) : IAppConfig {

    // region Api key

    override suspend fun getApiKey(): String? {
        return prefs.getString(Keys.API_KEY, null)
    }

    override suspend fun saveApiKey(apiKey: String) {
        prefs.edit {
            putString(Keys.API_KEY, apiKey)
        }
    }

    override suspend fun deleteApiKey() {
        prefs.edit {
            remove(Keys.API_KEY)
        }
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
            if (eTag != null) {
                putString(Keys.SYNC_GRAMMAR_POINTS_ETAG, eTag)
            } else {
                remove(Keys.SYNC_GRAMMAR_POINTS_ETAG)
            }
        }
    }

    // endregion
}
