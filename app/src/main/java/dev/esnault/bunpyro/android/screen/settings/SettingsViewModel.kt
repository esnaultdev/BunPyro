package dev.esnault.bunpyro.android.screen.settings

import dev.esnault.bunpyro.android.screen.ScreenConfig
import dev.esnault.bunpyro.android.screen.base.BaseViewModel


class SettingsViewModel : BaseViewModel() {

    // region Events

    fun onDisplayClick() {
        navigate(SettingsFragmentDirections.actionSettingsToSettingsDisplay())
    }

    fun onReviewClick() {
        navigate(SettingsFragmentDirections.actionSettingsToSettingsReview())
    }

    fun onNotificationsClick() {
        navigate(SettingsFragmentDirections.actionSettingsToSettingsNotifications())
    }

    fun onUserClick() {
        navigate(SettingsFragmentDirections.actionSettingsToSettingsUser())
    }

    fun onDebugClick() {
        navigate(SettingsFragmentDirections.actionSettingsToSettingsDebug())
    }

    fun onAboutClick() {
        navigate(SettingsFragmentDirections.actionSettingsToSettingsAbout())
    }

    fun onPrivacyClick() {
        navigator.openUrlInBrowser(ScreenConfig.Url.privacy)
    }

    fun onLicencesClick() {
        navigate(SettingsFragmentDirections.actionSettingsToSettingsLicenses())
    }

    // endregion
}
