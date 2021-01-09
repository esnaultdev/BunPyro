package dev.esnault.bunpyro.android.screen.settings.debug


import android.os.Bundle
import android.view.View
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreference
import com.jakewharton.processphoenix.ProcessPhoenix
import dev.esnault.bunpyro.R
import dev.esnault.bunpyro.android.screen.base.BaseFragment
import dev.esnault.bunpyro.android.screen.base.BaseViewModel
import dev.esnault.bunpyro.android.utils.setupWithNav
import dev.esnault.bunpyro.databinding.FragmentSettingsDebugBinding
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SettingsDebugFragment : BaseFragment<FragmentSettingsDebugBinding>() {

    override val vm: BaseViewModel? = null
    override val bindingClass = FragmentSettingsDebugBinding::class

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val navController = findNavController()
        binding.toolbar.setupWithNav(navController)

        val prefFragment = PreferenceFragment().apply {
            this.navController = navController
        }
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, prefFragment)
            .commit()
    }

    /**
     * Since we can't add a toolbar to the preference fragment directly,
     * it's wrapped into another fragment.
     */
    class PreferenceFragment : PreferenceFragmentCompat() {

        var navController: NavController? = null

        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.settings_debug, rootKey)

            setupPreferences()
        }

        private fun setupPreferences() {
            findPreference<SwitchPreference>("debug_mock")?.apply {
                setOnPreferenceChangeListener { _, _ ->
                    GlobalScope.launch {
                        delay(300L)
                        ProcessPhoenix.triggerRebirth(requireContext())
                    }
                    true
                }
            }
        }
    }
}
