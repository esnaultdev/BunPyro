package dev.esnault.bunpyro.android.screen.settings

import androidx.preference.Preference
import dev.esnault.bunpyro.BuildConfig
import dev.esnault.bunpyro.R
import dev.esnault.bunpyro.android.screen.ScreenConfig
import dev.esnault.bunpyro.common.openUrlInBrowser
import org.koin.androidx.viewmodel.ext.android.viewModel


class SettingsFragment : BaseSettingsFragment() {

    override val vm: SettingsViewModel by viewModel()
    override val settingResId: Int = R.xml.settings_root
    override val toolbarTitleResId: Int = R.string.settings_root_title

    override fun SettingsPreferenceFragment.setupPreferences() {
        setupNavigation()
        setupAbout()
    }

    private fun SettingsPreferenceFragment.setupNavigation() {
        findPreference<Preference>("category_display")?.setOnPreferenceClickListener {
            vm.onDisplayClick()
            true
        }

        findPreference<Preference>("category_review")?.setOnPreferenceClickListener {
            vm.onReviewClick()
            true
        }

        findPreference<Preference>("category_notifications")?.setOnPreferenceClickListener {
            vm.onNotificationsClick()
            true
        }

        findPreference<Preference>("category_user")?.setOnPreferenceClickListener {
            vm.onUserClick()
            true
        }

        findPreference<Preference>("about_debug")?.apply {
            isVisible = BuildConfig.DEBUG
            setOnPreferenceClickListener {
                vm.onDebugClick()
                true
            }
        }
    }

    private fun SettingsPreferenceFragment.setupAbout() {
        findPreference<Preference>("about_app")?.setOnPreferenceClickListener {
            vm.onAboutClick()
            true
        }

        findPreference<Preference>("about_privacy")?.setOnPreferenceClickListener {
            context?.openUrlInBrowser(ScreenConfig.Url.privacy)
            true
        }

        findPreference<Preference>("about_licenses")?.setOnPreferenceClickListener {
            vm.onLicencesClick()
            true
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
}
