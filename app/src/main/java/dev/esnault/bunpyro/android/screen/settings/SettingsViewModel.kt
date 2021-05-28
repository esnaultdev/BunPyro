package dev.esnault.bunpyro.android.screen.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dev.esnault.bunpyro.android.screen.base.BaseViewModel
import dev.esnault.bunpyro.android.screen.base.SingleLiveEvent
import dev.esnault.bunpyro.data.config.IAppConfig
import dev.esnault.bunpyro.data.repository.apikey.ApiKeyCheckResult
import dev.esnault.bunpyro.data.repository.apikey.IApiKeyRepository
import dev.esnault.bunpyro.data.service.auth.IAuthService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch


class SettingsViewModel(
    private val appConfig: IAppConfig,
    private val apiKeyRepo: IApiKeyRepository,
    private val authService: IAuthService
) : BaseViewModel() {

    private val _viewState = MutableLiveData<ViewState>()
    val viewState: LiveData<ViewState>
        get() = _viewState

    private val _snackbar = SingleLiveEvent<SnackBarMessage>()
    val snackbar: LiveData<SnackBarMessage>
        get() = _snackbar

    private var refreshUserNameJob: Job? = null
    private var logoutJob: Job? = null

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
                val apiKey = apiKeyRepo.getApiKey()
                if (apiKey != null) { // We can't open this screen without one.
                    val checkResult = apiKeyRepo.checkApiKey(apiKey)
                    val userName = (checkResult as? ApiKeyCheckResult.Success)?.userInfo?.userName
                    if (userName != null) {
                        _viewState.postValue(ViewState(userName))
                    } else {
                        _snackbar.postValue(SnackBarMessage.UsernameFetchError)
                    }
                }
            }
        }
    }

    fun onLogoutClick() {
        if (logoutJob?.isActive != true) {
            // Theoretically, we should display a blocking dialog to not let the user do anything.
            // But in practice, clearing the user data is blazing fast.
            logoutJob = viewModelScope.launch(Dispatchers.IO) {
                if (authService.logout()) {
                    navigate(SettingsFragmentDirections.actionSettingsToApiKey())
                } else {
                    _snackbar.postValue(SnackBarMessage.LogoutError)
                }
            }
        }
    }

    // endregion

    data class ViewState(
        val userName: String?
    )

    sealed class SnackBarMessage {
        object UsernameFetchError : SnackBarMessage()
        object LogoutError : SnackBarMessage()
    }
}
