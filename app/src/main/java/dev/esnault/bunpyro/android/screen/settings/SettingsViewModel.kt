package dev.esnault.bunpyro.android.screen.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dev.esnault.bunpyro.android.screen.base.BaseViewModel
import dev.esnault.bunpyro.data.config.IAppConfig
import dev.esnault.bunpyro.data.repository.apikey.ApiKeyCheckResult
import dev.esnault.bunpyro.data.repository.apikey.IApiKeyRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch


class SettingsViewModel(
    private val appConfig: IAppConfig,
    private val apiKeyRepository: IApiKeyRepository
) : BaseViewModel() {

    private val _viewState = MutableLiveData<ViewState>()
    val viewState: LiveData<ViewState> = _viewState

    private var refreshUserNameJob: Job? = null

    init {
        viewModelScope.launch {
            val userName = appConfig.getUserName()
            _viewState.postValue(ViewState(userName = userName))
        }
    }

    // region Events

    fun onUserNameClick() {
        if (refreshUserNameJob?.isActive != true && _viewState.value?.userName == null) {
            refreshUserNameJob = viewModelScope.launch {
                val apiKey = apiKeyRepository.getApiKey()
                if (apiKey != null) { // We can't open this screen without one.
                    val checkResult = apiKeyRepository.checkApiKey(apiKey)
                    val userName = (checkResult as? ApiKeyCheckResult.Success)?.userInfo?.userName
                    if (userName != null) {
                        _viewState.postValue(ViewState(userName))
                    }
                }
            }
        }
    }

    fun onLogoutClick() {
        // TODO Clear all data and navigate to the start screen again
    }

    // endregion

    data class ViewState(
        val userName: String?
    )
}
