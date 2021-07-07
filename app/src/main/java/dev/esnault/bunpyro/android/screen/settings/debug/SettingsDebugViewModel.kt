package dev.esnault.bunpyro.android.screen.settings.debug

import androidx.lifecycle.viewModelScope
import dev.esnault.bunpyro.android.screen.base.BaseViewModel
import dev.esnault.bunpyro.data.config.IAppConfig
import dev.esnault.bunpyro.data.repository.settings.ISettingsRepository
import dev.esnault.bunpyro.domain.entities.settings.MockSubscriptionSetting
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class SettingsDebugViewModel(
    private val appConfig: IAppConfig,
    private val settingsRepository: ISettingsRepository
) : BaseViewModel() {

    // region Events

    fun onMockSubscriptionChange(newValue: MockSubscriptionSetting) {
        settingsRepository.setMockSubscription(newValue)
    }

    fun onClearGrammarEtag() {
        viewModelScope.launch(Dispatchers.IO) {
            appConfig.saveExampleSentencesEtag(null)
            appConfig.saveSupplementalLinksEtag(null)
            appConfig.saveGrammarPointsEtag(null)
        }
    }

    fun onClearReviewEtag() {
        viewModelScope.launch(Dispatchers.IO) {
            appConfig.saveReviewsEtag(null)
        }
    }

    // endregion
}
