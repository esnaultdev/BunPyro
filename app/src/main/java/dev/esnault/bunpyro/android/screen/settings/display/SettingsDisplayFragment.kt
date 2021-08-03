package dev.esnault.bunpyro.android.screen.settings.display

import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.ListPreference
import dev.esnault.bunpyro.R
import dev.esnault.bunpyro.android.res.toNightMode
import dev.esnault.bunpyro.android.screen.base.BaseViewModel
import dev.esnault.bunpyro.android.screen.settings.BaseSettingsFragment
import dev.esnault.bunpyro.android.screen.settings.SettingsPreferenceFragment
import dev.esnault.bunpyro.domain.entities.settings.NightModeSetting


class SettingsDisplayFragment : BaseSettingsFragment() {

    override val vm: BaseViewModel? = null
    override val settingResId: Int = R.xml.settings_display
    override val toolbarTitleResId: Int = R.string.settings_display_title

    override fun SettingsPreferenceFragment.setupPreferences() {
        findPreference<ListPreference>("night_mode")?.apply {
            summaryProvider = ListPreference.SimpleSummaryProvider.getInstance()
            setOnPreferenceChangeListener { _, newValue ->
                val setting = NightModeSetting.fromValue(newValue as? String)
                val nightMode = setting.toNightMode()
                AppCompatDelegate.setDefaultNightMode(nightMode)
                true
            }
        }

        findPreference<ListPreference>("furigana_default")?.apply {
            summaryProvider = ListPreference.SimpleSummaryProvider.getInstance()
        }

        findPreference<ListPreference>("review_hint_level")?.apply {
            summaryProvider = ListPreference.SimpleSummaryProvider.getInstance()
        }

        findPreference<ListPreference>("example_details")?.apply {
            summaryProvider = ListPreference.SimpleSummaryProvider.getInstance()
        }

        findPreference<ListPreference>("hanko_display")?.apply {
            summaryProvider = ListPreference.SimpleSummaryProvider.getInstance()
        }
    }
}
