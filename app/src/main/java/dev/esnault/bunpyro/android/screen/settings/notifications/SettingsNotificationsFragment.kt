package dev.esnault.bunpyro.android.screen.settings.notifications

import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.SwitchPreference
import dev.esnault.bunpyro.BuildConfig
import dev.esnault.bunpyro.R
import dev.esnault.bunpyro.android.screen.base.BaseViewModel
import dev.esnault.bunpyro.android.screen.settings.BaseSettingsFragment
import dev.esnault.bunpyro.android.screen.settings.SettingsPreferenceFragment


class SettingsNotificationsFragment : BaseSettingsFragment() {

    override val vm: BaseViewModel? = null
    override val settingResId: Int = R.xml.settings_notifications
    override val toolbarTitleResId: Int = R.string.settings_notifications_title

    override fun SettingsPreferenceFragment.setupPreferences() {
        val hasNotificationChannel = BuildConfig.VERSION_CODE >= 26

        val notifsEnabledPref = findPreference<SwitchPreference>("notification_reviews_enabled")
        val osSettingsPref = findPreference<Preference>("notification_reviews_osSetting")

        notifsEnabledPref?.isVisible = !hasNotificationChannel
        osSettingsPref?.isVisible = hasNotificationChannel

        findPreference<ListPreference>("notification_reviews_threshold")?.apply {
            summaryProvider = Preference.SummaryProvider<ListPreference> { preference ->
                val value = preference.value.toIntOrNull() ?: return@SummaryProvider ""
                resources.getQuantityString(
                    /* resId */ R.plurals.settings_notifications_reviews_threshold_summary,
                    /* quantity */ value,
                    /* formatArg */ value
                )
            }
        }

        findPreference<ListPreference>("notification_reviews_refresh")?.apply {
            summaryProvider = Preference.SummaryProvider<ListPreference> { preference ->
                val entry = preference.entry ?: return@SummaryProvider ""
                resources.getString(R.string.settings_notifications_reviews_refresh_summary, entry)
            }
        }
    }
}
