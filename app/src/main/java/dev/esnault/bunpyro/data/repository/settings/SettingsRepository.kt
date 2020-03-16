package dev.esnault.bunpyro.data.repository.settings

import android.content.Context
import androidx.preference.PreferenceManager
import dev.esnault.bunpyro.domain.entities.settings.NightModeSetting


class SettingsRepository(context: Context) : ISettingsRepository {

    private val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

    override suspend fun getNightMode(): NightModeSetting {
        val value = sharedPreferences.getString("night_mode", "system")
        return NightModeSetting.fromValue(value)
    }
}
