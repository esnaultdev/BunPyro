package dev.esnault.bunpyro.android.screen.start


import androidx.lifecycle.viewModelScope
import dev.esnault.bunpyro.android.screen.base.BaseViewModel
import dev.esnault.bunpyro.data.repository.apikey.IApiKeyRepository
import dev.esnault.bunpyro.data.repository.sync.ISyncRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class StartViewModel(
    private val apiKeyRepo: IApiKeyRepository,
    private val syncRepo: ISyncRepository
) : BaseViewModel() {

    init {
        viewModelScope.launch(Dispatchers.IO) {
            val hasApiKey = apiKeyRepo.hasApiKey()

            if (!hasApiKey) {
                navigate(StartFragmentDirections.actionStartToApiKey())
                return@launch
            }

            val firstSyncCompleted = syncRepo.getFirstSyncCompleted()

            if (!firstSyncCompleted) {
                navigate(StartFragmentDirections.actionStartToFirstSync())
            } else {
                navigate(StartFragmentDirections.actionStartToHome())
            }
        }
    }
}
