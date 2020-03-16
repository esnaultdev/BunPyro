package dev.esnault.bunpyro.android.screen.settings

import android.os.Bundle
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat

import dev.esnault.bunpyro.R
import dev.esnault.bunpyro.android.screen.ScreenConfig
import dev.esnault.bunpyro.common.openUrlInBrowser


class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.settings_root, rootKey)

        setupActions()
    }

    private fun setupActions() {
        findPreference<Preference>("about_app")?.setOnPreferenceClickListener {

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
