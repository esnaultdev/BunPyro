package dev.esnault.bunpyro.android.screen.settings

import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat

import dev.esnault.bunpyro.R
import dev.esnault.bunpyro.android.res.toNightMode
import dev.esnault.bunpyro.android.screen.ScreenConfig
import dev.esnault.bunpyro.common.openUrlInBrowser
import dev.esnault.bunpyro.domain.entities.settings.NightModeSetting


class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.settings_root, rootKey)

        setupDisplay()
        setupAbout()
    }

    private fun setupDisplay() {
        findPreference<ListPreference>("night_mode")?.apply {
            summaryProvider = ListPreference.SimpleSummaryProvider.getInstance()
            setOnPreferenceChangeListener { _, newValue ->
                val setting = NightModeSetting.fromValue(newValue as? String)
                val nightMode = setting.toNightMode()
                AppCompatDelegate.setDefaultNightMode(nightMode)
                true
            }
        }
    }

    private fun setupAbout() {
        findPreference<Preference>("about_app")?.setOnPreferenceClickListener {
            navigate(SettingsFragmentDirections.actionSettingsToSettingsAbout())
            true
        }

        findPreference<Preference>("about_privacy")?.setOnPreferenceClickListener {
            context?.openUrlInBrowser(ScreenConfig.privacyUrl)
            true
        }

        findPreference<Preference>("about_licenses")?.setOnPreferenceClickListener {
            navigate(SettingsFragmentDirections.actionSettingsToSettingsLicenses())
            true
        }
    }

    private fun navigate(navDirections: NavDirections) {
        findNavController().navigate(navDirections)
    }
}
