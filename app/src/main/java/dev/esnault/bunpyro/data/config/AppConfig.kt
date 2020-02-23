package dev.esnault.bunpyro.data.config

import android.content.SharedPreferences
import androidx.core.content.edit

private object Keys {
    const val API_KEY = "ApiKey"
}


class AppConfig(private val prefs: SharedPreferences) : IAppConfig {

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
}
