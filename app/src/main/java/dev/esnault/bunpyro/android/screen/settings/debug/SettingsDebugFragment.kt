package dev.esnault.bunpyro.android.screen.settings.debug

import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.SwitchPreference
import com.jakewharton.processphoenix.ProcessPhoenix
import dev.esnault.bunpyro.R
import dev.esnault.bunpyro.android.screen.settings.BaseSettingsFragment
import dev.esnault.bunpyro.android.screen.settings.SettingsPreferenceFragment
import dev.esnault.bunpyro.domain.entities.settings.MockSubscriptionSetting
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel


class SettingsDebugFragment : BaseSettingsFragment<SettingsDebugViewModel>() {

    override val vm: SettingsDebugViewModel by viewModel()

    override val settingResId: Int = R.xml.settings_debug

    override val toolbarTitleResId: Int = R.string.settings_debug_title

    override fun SettingsPreferenceFragment.setupPreferences() {
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
                vm.onMockSubscriptionChange(MockSubscriptionSetting.fromValue(stringValue))
                true
            }
        }

        findPreference<Preference>("debug_clearGrammarEtag")?.setOnPreferenceClickListener {
            vm.onClearGrammarEtag()
            true
        }

        findPreference<Preference>("debug_clearReviewEtag")?.setOnPreferenceClickListener {
            vm.onClearReviewEtag()
            true
        }
    }
}
