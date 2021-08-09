package dev.esnault.bunpyro.android.screen.settings.licenses

import dev.esnault.bunpyro.android.screen.base.BaseViewModel


class SettingsLicensesViewModel: BaseViewModel() {

    // region Events

    fun onLicenseClick(license: License) {
        navigator.openUrlInBrowser(license.url)
    }

    // endregion

}
