package dev.esnault.bunpyro.android.screen.start


import dev.esnault.bunpyro.android.screen.base.BaseViewModel
import dev.esnault.bunpyro.data.repository.apikey.IApiKeyRepository
import dev.esnault.bunpyro.data.repository.sync.ISyncRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class StartViewModel(
    private val apiKeyRepo: IApiKeyRepository,
    private val syncRepo: ISyncRepository
) : BaseViewModel() {

    init {
        GlobalScope.launch {
            val hasApiKey = withContext(Dispatchers.IO) {
                apiKeyRepo.hasApiKey()
            }

            if (!hasApiKey) {
                navigate(StartFragmentDirections.actionStartToApiKey())
                return@launch
            }

            val firstSyncCompleted = withContext(Dispatchers.IO) {
                syncRepo.getFirstSyncCompleted()
            }

            if (!firstSyncCompleted) {
                navigate(StartFragmentDirections.actionStartToFirstSync())
            } else {
                navigate(StartFragmentDirections.actionStartToHome())
            }
        }
    }
}
