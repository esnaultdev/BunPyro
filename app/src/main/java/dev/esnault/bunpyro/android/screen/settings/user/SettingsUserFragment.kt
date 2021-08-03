package dev.esnault.bunpyro.android.screen.settings.user

import android.os.Bundle
import android.view.View
import androidx.preference.Preference
import com.google.android.material.snackbar.Snackbar
import dev.esnault.bunpyro.R
import dev.esnault.bunpyro.android.screen.settings.BaseSettingsFragment
import dev.esnault.bunpyro.android.screen.settings.SettingsPreferenceFragment
import dev.esnault.bunpyro.android.screen.settings.user.SettingsUserViewModel.SnackBarMessage
import dev.esnault.bunpyro.android.utils.safeObserve
import dev.esnault.bunpyro.domain.entities.user.SubscriptionStatus
import org.koin.androidx.viewmodel.ext.android.viewModel


class SettingsUserFragment : BaseSettingsFragment() {

    override val vm: SettingsUserViewModel by viewModel()
    override val settingResId: Int = R.xml.settings_user
    override val toolbarTitleResId: Int = R.string.settings_user_title

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        vm.apply {
            viewState.safeObserve(this@SettingsUserFragment) { viewState ->
                preferencesFragment.bindState(viewState)
            }
            snackbar.safeObserve(this@SettingsUserFragment, ::showSnackBar)
        }
    }

    override fun SettingsPreferenceFragment.setupPreferences() {
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

    private fun SettingsPreferenceFragment.bindState(viewState: SettingsUserViewModel.ViewState) {
        findPreference<Preference>("user_name")?.apply {
            summary = viewState.userName ?: getString(R.string.settings_user_name_sync)
        }
        findPreference<Preference>("user_subscription")?.apply {
            val resId = when (viewState.subStatus) {
                SubscriptionStatus.SUBSCRIBED ->
                    R.string.settings_user_subscription_subscribed
                SubscriptionStatus.NOT_SUBSCRIBED ->
                    R.string.settings_user_subscription_notSubscribed
                SubscriptionStatus.EXPIRED ->
                    R.string.settings_user_subscription_expired
            }
            summary = getString(resId)
        }
    }

    // region Snackbar

    private fun showSnackBar(message: SnackBarMessage) {
        val view = view ?: return
        val textResId = when (message) {
            is SnackBarMessage.UsernameFetchError -> R.string.settings_user_name_sync_error
            is SnackBarMessage.LogoutError -> R.string.settings_user_logout_error
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
