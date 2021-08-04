package dev.esnault.bunpyro.android.screen.settings.review

import androidx.preference.ListPreference
import dev.esnault.bunpyro.R
import dev.esnault.bunpyro.android.screen.base.BaseViewModel
import dev.esnault.bunpyro.android.screen.settings.BaseSettingsFragment
import dev.esnault.bunpyro.android.screen.settings.SettingsPreferenceFragment


class SettingsReviewFragment : BaseSettingsFragment() {

    override val vm: BaseViewModel? = null
    override val settingResId: Int = R.xml.settings_review
    override val toolbarTitleResId: Int = R.string.settings_review_title

    override fun SettingsPreferenceFragment.setupPreferences() {
        findPreference<ListPreference>("review_hint_level")?.apply {
            summaryProvider = ListPreference.SimpleSummaryProvider.getInstance()
        }
    }
}
