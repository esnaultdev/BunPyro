package dev.esnault.bunpyro.android.screen.settings

import dev.esnault.bunpyro.android.screen.base.BaseViewModel


class SettingsViewModel : BaseViewModel() {

    // region Events

    fun onDisplayClick() {
        navigate(SettingsFragmentDirections.actionSettingsToSettingsDisplay())
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

    fun onLicencesClick() {
        navigate(SettingsFragmentDirections.actionSettingsToSettingsLicenses())
    }

    // endregion
}
