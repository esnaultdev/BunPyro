package dev.esnault.bunpyro.android.screen.start


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController

import dev.esnault.bunpyro.R
import dev.esnault.bunpyro.data.config.IAppConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.android.ext.android.inject


/**
 * A start fragment used for the initial navigation.
 */
class StartFragment : Fragment() {

    val appConfig: IAppConfig by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        GlobalScope.launch {
            val apiKey = appConfig.getApiKey()
            withContext(Dispatchers.Main) {
                navigateToNextScreen(apiKey != null)
            }
        }
    }

    private fun navigateToNextScreen(hasApiKey: Boolean) {
        // TODO navigate to the home screen if we have an API key
        findNavController().navigate(R.id.action_startFragment_to_apiKeyFragment)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_start, container, false)
    }
}
