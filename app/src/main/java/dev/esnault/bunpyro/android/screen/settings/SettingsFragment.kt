package dev.esnault.bunpyro.android.screen.settings

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatDelegate
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
import dev.esnault.bunpyro.android.utils.setupWithNav
import dev.esnault.bunpyro.common.openUrlInBrowser
import dev.esnault.bunpyro.data.analytics.Analytics
import dev.esnault.bunpyro.databinding.FragmentSettingsBinding
import dev.esnault.bunpyro.domain.entities.settings.NightModeSetting
import org.koin.androidx.viewmodel.ext.android.viewModel


class SettingsFragment : BaseFragment<FragmentSettingsBinding>() {

    override val vm: SettingsViewModel by viewModel()
    override val bindingClass = FragmentSettingsBinding::class

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Analytics.screen(name = "settings")

        binding.toolbar.setupWithNav(findNavController())

        val prefFragment = PreferenceFragment().apply {
            this.vm = this@SettingsFragment.vm
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

        var vm: SettingsViewModel? = null

        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.settings_root, rootKey)

            setupDisplay()
            setupUser()
            setupAbout()
        }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)
            vm?.viewState?.observe(viewLifecycleOwner, this::bindState)
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

            findPreference<ListPreference>("review_hint_level")?.apply {
                summaryProvider = ListPreference.SimpleSummaryProvider.getInstance()
            }

            findPreference<ListPreference>("example_details")?.apply {
                summaryProvider = ListPreference.SimpleSummaryProvider.getInstance()
            }

            findPreference<ListPreference>("hanko_display")?.apply {
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

        private fun setupUser() {
            findPreference<Preference>("user_name")?.apply {
                summary = getString(R.string.settings_root_user_name_default)
                setOnPreferenceClickListener {
                    vm?.onUserNameClick()
                    true
                }
            }
            findPreference<Preference>("user_logout")?.setOnPreferenceClickListener {
                vm?.onLogoutClick()
                true
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

            findPreference<Preference>("about_debug")?.apply {
                isVisible = BuildConfig.DEBUG
                setOnPreferenceClickListener {
                    navigate(SettingsFragmentDirections.actionSettingsToSettingsDebug())
                    true
                }
            }
        }

        private fun bindState(viewState: SettingsViewModel.ViewState) {
            findPreference<Preference>("user_name")?.apply {
                summary = viewState.userName ?: getString(R.string.settings_root_user_name_default)
            }
        }

        private fun navigate(navDirections: NavDirections) {
            vm?.navigate(navDirections)
        }
    }
}
