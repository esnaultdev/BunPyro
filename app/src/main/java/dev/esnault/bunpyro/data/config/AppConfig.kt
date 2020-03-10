package dev.esnault.bunpyro.data.config

import android.content.SharedPreferences
import androidx.core.content.edit

private object Keys {
    const val API_KEY = "ApiKey"
    const val FIRST_SYNC_COMPLETED = "FirstSyncCompleted"
    const val SYNC_EXAMPLE_SENTENCES_ETAG = "ExampleSentencesEtag"
    const val SYNC_GRAMMAR_POINTS_ETAG = "SyncGrammarPointsEtag"
    const val SYNC_REVIEWS_ETAG = "ReviewsEtag"
    const val SYNC_SUPPLEMENTAL_LINKS_ETAG = "SupplementalLinksEtag"
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

    override suspend fun getExampleSentencesEtag(): String? {
        return prefs.getString(Keys.SYNC_EXAMPLE_SENTENCES_ETAG, null)
    }

    override suspend fun saveExampleSentencesEtag(eTag: String?) {
        prefs.edit {
            if (eTag != null) {
                putString(Keys.SYNC_EXAMPLE_SENTENCES_ETAG, eTag)
            } else {
                remove(Keys.SYNC_EXAMPLE_SENTENCES_ETAG)
            }
        }
    }

    override suspend fun getReviewsEtag(): String? {
        return prefs.getString(Keys.SYNC_REVIEWS_ETAG, null)
    }

    override suspend fun saveReviewsEtag(eTag: String?) {
        prefs.edit {
            if (eTag != null) {
                putString(Keys.SYNC_REVIEWS_ETAG, eTag)
            } else {
                remove(Keys.SYNC_REVIEWS_ETAG)
            }
        }
    }

    override suspend fun getSupplementalLinksEtag(): String? {
        return prefs.getString(Keys.SYNC_SUPPLEMENTAL_LINKS_ETAG, null)
    }

    override suspend fun saveSupplementalLinksEtag(eTag: String?) {
        prefs.edit {
            if (eTag != null) {
                putString(Keys.SYNC_SUPPLEMENTAL_LINKS_ETAG, eTag)
            } else {
                remove(Keys.SYNC_SUPPLEMENTAL_LINKS_ETAG)
            }
        }
    }

    // endregion
}
