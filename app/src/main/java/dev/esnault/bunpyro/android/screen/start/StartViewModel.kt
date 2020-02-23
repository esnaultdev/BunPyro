package dev.esnault.bunpyro.android.screen.start


import dev.esnault.bunpyro.android.screen.base.BaseViewModel
import dev.esnault.bunpyro.data.repository.apikey.IApiKeyRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class StartViewModel(
    private val apiKeyRepo: IApiKeyRepository
) : BaseViewModel() {

    init {
        GlobalScope.launch {
            val hasApiKey = withContext(Dispatchers.IO) {
                apiKeyRepo.hasApiKey()
            }
            navigateToNextScreen(hasApiKey)
        }
    }

    private fun navigateToNextScreen(hasApiKey: Boolean) {
        val navDirections = if (hasApiKey) {
            StartFragmentDirections.actionStartToHome()
        } else {
            StartFragmentDirections.actionStartToApiKey()
        }
        navigate(navDirections)
    }
}
