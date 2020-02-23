package dev.esnault.bunpyro.android.screen.apikey

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.viewModelScope
import dev.esnault.bunpyro.android.screen.base.BaseViewModel
import dev.esnault.bunpyro.data.repository.apikey.IApiKeyRepository
import kotlinx.coroutines.launch


class ApiKeyViewModel(
    private val apiKeyRepository: IApiKeyRepository
) : BaseViewModel() {

    private val _canSend = MutableLiveData<Boolean>()
    val canSend: LiveData<Boolean>
        get() = Transformations.distinctUntilChanged(_canSend)

    private var apiKey: String? = null

    init {
        _canSend.postValue(false)
    }

    fun apiKeyUpdated(apiKey: String?) {
        this.apiKey = apiKey
        _canSend.postValue(!apiKey.isNullOrBlank())
    }

    fun saveApiKey() {
        val apiKey = apiKey ?: return

        viewModelScope.launch {
            apiKeyRepository.checkAndSaveApiKey(apiKey)
            navigate(ApiKeyFragmentDirections.actionApiKeyToHome())
        }
    }
}
