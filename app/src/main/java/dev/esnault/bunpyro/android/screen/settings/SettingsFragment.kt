package dev.esnault.bunpyro.android.screen.settings

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.afollestad.materialdialogs.MaterialDialog
import com.google.android.material.snackbar.Snackbar
import dev.esnault.bunpyro.BuildConfig
import dev.esnault.bunpyro.R
import dev.esnault.bunpyro.android.res.toNightMode
import dev.esnault.bunpyro.android.screen.ScreenConfig
import dev.esnault.bunpyro.android.screen.base.BaseFragment
import dev.esnault.bunpyro.android.screen.settings.SettingsViewModel.SnackBarMessage
import dev.esnault.bunpyro.android.utils.safeObserve
import dev.esnault.bunpyro.android.utils.setupWithNav
import dev.esnault.bunpyro.common.openUrlInBrowser
import dev.esnault.bunpyro.data.analytics.Analytics
import dev.esnault.bunpyro.databinding.FragmentSettingsBinding
import dev.esnault.bunpyro.domain.entities.settings.NightModeSetting
import dev.esnault.bunpyro.domain.entities.user.SubscriptionStatus
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

        private var dialog: MaterialDialog? = null

        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.settings_root, rootKey)

            setupDisplay()
            setupUser()
            setupAbout()
        }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)

            vm?.apply {
                viewState.safeObserve(this@PreferenceFragment, ::bindState)
                snackbar.safeObserve(this@PreferenceFragment, ::showSnackBar)
            }
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
            findPreference<Preference>("user_name")?.setOnPreferenceClickListener {
                vm?.onUserNameClick()
                true
            }
            findPreference<Preference>("user_logout")?.setOnPreferenceClickListener {
                vm?.onLogoutClick()
                true
            }
            findPreference<Preference>("user_subscription")?.setOnPreferenceClickListener {
                vm?.onSubscriptionClick()
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
                        is SnackBarMessage.UsernameFetchError -> vm?.onUserNameClick()
                        is SnackBarMessage.LogoutError -> vm?.onLogoutClick()
                    }
                }
                .show()
        }

        // endregion

        private fun navigate(navDirections: NavDirections) {
            vm?.navigate(navDirections)
        }
    }
}
