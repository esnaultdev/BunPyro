package dev.esnault.bunpyro.android.screen.settings.notifications

import dev.esnault.bunpyro.android.display.notification.INotificationService
import dev.esnault.bunpyro.android.screen.base.BaseViewModel


class SettingsNotificationsViewModel(
    private val notificationService: INotificationService
): BaseViewModel() {

    // region Events

    fun onReviewsClick() {
        notificationService.openReviewsNativeSettings()
    }

    // endregion

}
