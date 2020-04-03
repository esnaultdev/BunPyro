package dev.esnault.bunpyro.android.screen.settings

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import dev.esnault.bunpyro.BuildConfig
import dev.esnault.bunpyro.R
import dev.esnault.bunpyro.android.res.toNightMode
import dev.esnault.bunpyro.android.screen.ScreenConfig
import dev.esnault.bunpyro.android.screen.base.BaseFragment
import dev.esnault.bunpyro.android.screen.base.BaseViewModel
import dev.esnault.bunpyro.android.utils.setupWithNav
import dev.esnault.bunpyro.common.openUrlInBrowser
import dev.esnault.bunpyro.databinding.FragmentSettingsBinding
import dev.esnault.bunpyro.domain.entities.settings.NightModeSetting


class SettingsFragment : BaseFragment<FragmentSettingsBinding>() {

    override val vm: BaseViewModel? = null
    override val bindingClass = FragmentSettingsBinding::class

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

            findPreference<ListPreference>("furigana_default")?.apply {
                summaryProvider = ListPreference.SimpleSummaryProvider.getInstance()
            }

            findPreference<ListPreference>("example_details")?.apply {
                summaryProvider = ListPreference.SimpleSummaryProvider.getInstance()
            }

            findPreference<Preference>("about_version")?.apply {
                val versionName = BuildConfig.VERSION_NAME
                val versionCode = BuildConfig.VERSION_CODE

                summary = if (BuildConfig.DEBUG) {
                    "$versionName ($versionCode) debug"
                } else {
                    "$versionName ($versionCode)"
                }
            }
        }

        private fun setupAbout() {
            findPreference<Preference>("about_app")?.setOnPreferenceClickListener {
                navigate(SettingsFragmentDirections.actionSettingsToSettingsAbout())
                true
            }

            findPreference<Preference>("about_privacy")?.setOnPreferenceClickListener {
                context?.openUrlInBrowser(ScreenConfig.Url.privacy)
                true
            }

            findPreference<Preference>("about_licenses")?.setOnPreferenceClickListener {
                navigate(SettingsFragmentDirections.actionSettingsToSettingsLicenses())
                true
            }
        }

        private fun navigate(navDirections: NavDirections) {
            navController?.navigate(navDirections)
        }
    }
}
