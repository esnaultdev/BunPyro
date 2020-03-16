package dev.esnault.bunpyro.data.repository.settings

import dev.esnault.bunpyro.domain.entities.settings.NightModeSetting


interface ISettingsRepository {
    suspend fun getNightMode(): NightModeSetting
}
