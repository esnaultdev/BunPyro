package dev.esnault.bunpyro.android.screen.settings

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.ListPreference
import androidx.preference.Preference
import com.google.android.material.snackbar.Snackbar
import dev.esnault.bunpyro.BuildConfig
import dev.esnault.bunpyro.R
import dev.esnault.bunpyro.android.res.toNightMode
import dev.esnault.bunpyro.android.screen.ScreenConfig
import dev.esnault.bunpyro.android.screen.settings.SettingsViewModel.SnackBarMessage
import dev.esnault.bunpyro.android.utils.safeObserve
import dev.esnault.bunpyro.common.openUrlInBrowser
import dev.esnault.bunpyro.domain.entities.settings.NightModeSetting
import dev.esnault.bunpyro.domain.entities.user.SubscriptionStatus
import org.koin.androidx.viewmodel.ext.android.viewModel


class SettingsFragment : BaseSettingsFragment() {

    override val vm: SettingsViewModel by viewModel()
    override val settingResId: Int = R.xml.settings_root
    override val toolbarTitleResId: Int = R.string.settings_root_title

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        vm.apply {
            viewState.safeObserve(this@SettingsFragment) { viewState ->
                preferencesFragment.bindState(viewState)
            }
            snackbar.safeObserve(this@SettingsFragment, ::showSnackBar)
        }
    }

    override fun SettingsPreferenceFragment.setupPreferences() {
        setupDisplay()
        setupUser()
        setupAbout()
    }

    private fun SettingsPreferenceFragment.setupDisplay() {
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

    private fun SettingsPreferenceFragment.setupUser() {
        findPreference<Preference>("user_name")?.setOnPreferenceClickListener {
            vm.onUserNameClick()
            true
        }
        findPreference<Preference>("user_logout")?.setOnPreferenceClickListener {
            vm.onLogoutClick()
            true
        }
        findPreference<Preference>("user_subscription")?.setOnPreferenceClickListener {
            vm.onSubscriptionClick()
            true
        }
    }

    private fun SettingsPreferenceFragment.setupAbout() {
        findPreference<Preference>("about_app")?.setOnPreferenceClickListener {
            vm.navigate(SettingsFragmentDirections.actionSettingsToSettingsAbout())
            true
        }

        findPreference<Preference>("about_privacy")?.setOnPreferenceClickListener {
            context?.openUrlInBrowser(ScreenConfig.Url.privacy)
            true
        }

        findPreference<Preference>("about_licenses")?.setOnPreferenceClickListener {
            vm.navigate(SettingsFragmentDirections.actionSettingsToSettingsLicenses())
            true
        }

        findPreference<Preference>("about_debug")?.apply {
            isVisible = BuildConfig.DEBUG
            setOnPreferenceClickListener {
                vm.navigate(SettingsFragmentDirections.actionSettingsToSettingsDebug())
                true
            }
        }
    }

    private fun SettingsPreferenceFragment.bindState(viewState: SettingsViewModel.ViewState) {
        findPreference<Preference>("user_name")?.apply {
            summary = viewState.userName ?: getString(R.string.settings_root_user_name_sync)
        }
        findPreference<Preference>("user_subscription")?.apply {
            val resId = when (viewState.subStatus) {
                SubscriptionStatus.SUBSCRIBED ->
                    R.string.settings_root_user_subscription_subscribed
                SubscriptionStatus.NOT_SUBSCRIBED ->
                    R.string.settings_root_user_subscription_notSubscribed
                SubscriptionStatus.EXPIRED ->
                    R.string.settings_root_user_subscription_expired
            }
            summary = getString(resId)
        }
    }

    // region Snackbar

    private fun showSnackBar(message: SnackBarMessage) {
        val view = view ?: return
        val textResId = when (message) {
            is SnackBarMessage.UsernameFetchError -> R.string.settings_root_user_name_sync_error
            is SnackBarMessage.LogoutError -> R.string.settings_root_user_logout_error
        }

        Snackbar.make(view, textResId, Snackbar.LENGTH_SHORT)
            .setAction(R.string.common_retry) {
                when (message) {
                    is SnackBarMessage.UsernameFetchError -> vm.onUserNameClick()
                    is SnackBarMessage.LogoutError -> vm.onLogoutClick()
                }
            }
            .show()
    }

    // endregion
}
