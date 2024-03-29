package dev.esnault.bunpyro.android.screen.apikey

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.viewModelScope
import dev.esnault.bunpyro.android.screen.ScreenConfig
import dev.esnault.bunpyro.android.screen.base.BaseViewModel
import dev.esnault.bunpyro.data.analytics.Analytics
import dev.esnault.bunpyro.data.repository.apikey.ApiKeyCheckResult
import dev.esnault.bunpyro.data.repository.apikey.IApiKeyRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class ApiKeyViewModel(
    private val apiKeyRepository: IApiKeyRepository
) : BaseViewModel() {

    private val _viewState = MutableLiveData<ViewState>()
    val viewState: LiveData<ViewState>
        get() = Transformations.distinctUntilChanged(_viewState)

    private var currentState: ViewState = ViewState.Default(canSend = false)
        set(value) {
            field = value
            _viewState.postValue(value)
        }

    private var apiKey: String? = null
    private var saveJob: Job? = null

    init {
        Analytics.screen(name = "apiKey")
        _viewState.postValue(ViewState.Default(false))
    }

    // Events

    fun apiKeyUpdated(apiKey: String?) {
        this.apiKey = apiKey
        if (currentState is ViewState.Default) {
            currentState = ViewState.Default(canSend(apiKey))
        }
    }

    fun onSaveApiKey() {
        val apiKey = apiKey ?: return
        if (saveJob?.isActive == true) return

        currentState = ViewState.Checking

        saveJob = viewModelScope.launch {
            val checkResult = apiKeyRepository.checkAndSaveApiKey(apiKey)
            handleApiKeyCheckResult(checkResult)
        }
    }

    fun onErrorOk() {
        currentState = ViewState.Default(canSend(apiKey))
    }

    fun onBunproWebsiteClick() {
        navigator.openUrlInBrowser(ScreenConfig.Url.bunpro)
    }

    fun onPrivacyClick() {
        navigator.openUrlInBrowser(ScreenConfig.Url.privacy)
    }

    // endregion

    private fun canSend(apiKey: String?): Boolean = !apiKey.isNullOrBlank()

    private suspend fun handleApiKeyCheckResult(checkResult: ApiKeyCheckResult) {
        when (checkResult) {
            is ApiKeyCheckResult.Success -> {
                currentState = ViewState.Success(checkResult.userInfo.userName)
                delay(2000L)
                navigate(ApiKeyFragmentDirections.actionApiKeyToFirstSync())
            }
            is ApiKeyCheckResult.Error -> {
                val newState = when (checkResult) {
                    ApiKeyCheckResult.Error.Network -> ViewState.Error.Network
                    ApiKeyCheckResult.Error.Invalid -> ViewState.Error.Invalid
                    ApiKeyCheckResult.Error.Server -> ViewState.Error.Server
                    is ApiKeyCheckResult.Error.Unknown -> ViewState.Error.Unknown
                }
                currentState = newState
            }
        }
    }

    sealed class ViewState {
        data class Default(val canSend: Boolean) : ViewState()
        object Checking: ViewState()
        data class Success(val name: String) : ViewState()
        sealed class Error : ViewState() {
            object Network : Error()
            object Invalid : Error()
            object Server : Error()
            object Unknown : Error()
        }
    }
}
