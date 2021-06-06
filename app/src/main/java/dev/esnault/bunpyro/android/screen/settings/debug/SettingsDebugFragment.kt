package dev.esnault.bunpyro.android.screen.settings.debug


import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.preference.ListPreference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreference
import com.jakewharton.processphoenix.ProcessPhoenix
import dev.esnault.bunpyro.R
import dev.esnault.bunpyro.android.screen.base.BaseFragment
import dev.esnault.bunpyro.android.utils.setupWithNav
import dev.esnault.bunpyro.databinding.FragmentSettingsDebugBinding
import dev.esnault.bunpyro.domain.entities.settings.MockSubscriptionSetting
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsDebugFragment : BaseFragment<FragmentSettingsDebugBinding>() {

    override val vm: SettingsDebugViewModel by viewModel()
    override val bindingClass = FragmentSettingsDebugBinding::class

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.setupWithNav(findNavController())

        val prefFragment = PreferenceFragment().apply {
            this.vm = this@SettingsDebugFragment.vm
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

        var vm: SettingsDebugViewModel? = null

        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.settings_debug, rootKey)

            setupPreferences()
        }

        private fun setupPreferences() {
            findPreference<SwitchPreference>("debug_mock")
                ?.setOnPreferenceChangeListener { _, _ ->
                    GlobalScope.launch {
                        delay(300L)
                        ProcessPhoenix.triggerRebirth(requireContext())
                    }
                    true
                }

            findPreference<ListPreference>("debug_forceSub")?.apply {
                summaryProvider = ListPreference.SimpleSummaryProvider.getInstance()
                setOnPreferenceChangeListener { _, newValue ->
                    val stringValue = newValue as? String
                    vm?.onMockSubscriptionChange(MockSubscriptionSetting.fromValue(stringValue))
                    true
                }
            }
        }
    }
}
