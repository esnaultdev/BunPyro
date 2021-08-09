package dev.esnault.bunpyro.android.screen.settings.about

import dev.esnault.bunpyro.android.screen.ScreenConfig
import dev.esnault.bunpyro.android.screen.base.BaseViewModel


class SettingsAboutViewModel: BaseViewModel() {

    // region Events

    fun onBunproClick() {
        navigator.openUrlInBrowser(ScreenConfig.Url.bunpro)
    }

    fun onDevWebsiteClick() {
        navigator.openUrlInBrowser(ScreenConfig.Url.devWebsite)
    }

    fun onGithubRepoClick() {
        navigator.openUrlInBrowser(ScreenConfig.Url.githubRepo)
    }

    // endregion

}
