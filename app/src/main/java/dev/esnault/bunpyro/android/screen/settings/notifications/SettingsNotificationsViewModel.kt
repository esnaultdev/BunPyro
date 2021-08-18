package dev.esnault.bunpyro.android.screen.settings.notifications

import androidx.lifecycle.viewModelScope
import dev.esnault.bunpyro.android.display.notification.INotificationService
import dev.esnault.bunpyro.android.screen.base.BaseViewModel
import dev.esnault.bunpyro.data.work.IWorkScheduler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class SettingsNotificationsViewModel(
    private val notificationService: INotificationService,
    private val workScheduler: IWorkScheduler,
): BaseViewModel() {

    // region Events

    fun onReviewsClick() {
        notificationService.openReviewsNativeSettings()
    }

    fun onReviewsRefreshRateChanged() {
        viewModelScope.launch(Dispatchers.IO) {
            workScheduler.rescheduleReviewCountWork()
        }
    }

    // endregion

}
