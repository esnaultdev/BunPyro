package dev.esnault.bunpyro.android.screen.settings.debug

import dev.esnault.bunpyro.android.screen.base.BaseViewModel
import dev.esnault.bunpyro.data.repository.settings.ISettingsRepository
import dev.esnault.bunpyro.domain.entities.settings.MockSubscriptionSetting


class SettingsDebugViewModel(
    private val settingsRepository: ISettingsRepository
) : BaseViewModel() {

    // region Events

    fun onMockSubscriptionChange(newValue: MockSubscriptionSetting) {
        settingsRepository.setMockSubscription(newValue)
    }

    // endregion
}
