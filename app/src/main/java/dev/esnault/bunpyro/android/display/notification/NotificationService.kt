package dev.esnault.bunpyro.android.display.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.navigation.NavDeepLinkBuilder
import dev.esnault.bunpyro.R
import dev.esnault.bunpyro.android.MainActivity
import dev.esnault.bunpyro.common.getThemeColor
import dev.esnault.bunpyro.common.openNotificationSettingsCompat
import dev.esnault.bunpyro.data.repository.settings.ISettingsRepository
import androidx.core.app.NotificationManagerCompat


class NotificationService(
    private val context: Context,
    private val settingsRepo: ISettingsRepository,
) : INotificationService {

    private val notificationManager = NotificationManagerCompat.from(context)

    object ChannelId {
        const val SYNC = "sync"
        const val REVIEWS = "reviews"
    }

    object NotificationId {
        const val SYNC = 1
        const val REVIEWS = 2
    }

    init {
        createChannels()
    }

    // region Channels

    private fun createChannels() {
        if (Build.VERSION.SDK_INT >= 26) {
            createChannel(
                channelId = ChannelId.SYNC,
                nameResId = R.string.notificationChannel_sync_name,
                descriptionResId = R.string.notificationChannel_sync_description,
                importance = NotificationManager.IMPORTANCE_LOW
            )
            createChannel(
                channelId = ChannelId.REVIEWS,
                nameResId = R.string.notificationChannel_reviews_name,
                descriptionResId = R.string.notificationChannel_reviews_description,
                importance = NotificationManager.IMPORTANCE_DEFAULT
            )
        }
    }

    @RequiresApi(26)
    private fun createChannel(
        channelId: String,
        nameResId: Int,
        descriptionResId: Int,
        importance: Int = NotificationManager.IMPORTANCE_DEFAULT
    ) {
        val name = context.getString(nameResId)
        val descriptionText = context.getString(descriptionResId)
        val channel = NotificationChannel(channelId, name, importance)
        channel.description = descriptionText

        notificationManager.createNotificationChannel(channel)
    }

    private fun areNotificationsEnabled(): Boolean {
        return notificationManager.areNotificationsEnabled()
    }

    @RequiresApi(26)
    private fun isNotificationChannelEnabled(channelId: String): Boolean {
        if (channelId.isBlank()) return false
        val channel = notificationManager.getNotificationChannel(channelId)
        return if (channel != null) {
            channel.importance != NotificationManager.IMPORTANCE_NONE
        } else {
            false
        }
    }

    // endregion

    // region Sync notification

    override fun buildSyncNotification(): NotificationWithId {
        val pendingIntent = Intent(context, MainActivity::class.java).let { notificationIntent ->
            PendingIntent.getActivity(context, 0, notificationIntent, 0)
        }

        return NotificationCompat.Builder(context, ChannelId.SYNC)
            .setContentTitle(context.getText(R.string.notification_sync_inProgress))
            .setSmallIcon(R.drawable.bunpyro_32dp)
            .setColor(context.getThemeColor(R.attr.colorPrimary))
            .setProgress(0, 0, true)
            .setContentIntent(pendingIntent)
            .build()
            .withId(NotificationId.SYNC)
    }

    override fun hideSyncNotification() {
        notificationManager.cancel(NotificationId.SYNC)
    }

    // endregion

    // region Reviews notification

    override fun showReviewsNotification(count: Int) {
        val pendingIntent = NavDeepLinkBuilder(context)
            .setGraph(R.navigation.nav_graph)
            .setDestination(R.id.reviewScreen)
            .createPendingIntent()

        val content = context.getString(R.string.notification_reviews_title, count)
        val notification = NotificationCompat.Builder(context, ChannelId.REVIEWS)
            .setContentTitle(content)
            .setSmallIcon(R.drawable.bunpyro_32dp)
            .setColor(context.getThemeColor(R.attr.colorPrimary))
            .setContentIntent(pendingIntent)
            .build()

        notificationManager.notify(NotificationId.REVIEWS, notification)
    }

    override fun hideReviewsNotification() {
        notificationManager.cancel(NotificationId.REVIEWS)
    }

    override fun openReviewsNativeSettings() {
        context.openNotificationSettingsCompat(ChannelId.REVIEWS)
    }

    override suspend fun isReviewsNotificationEnabled(): Boolean {
        if (!areNotificationsEnabled()) return false
        return if (Build.VERSION.SDK_INT >= 26) {
            isNotificationChannelEnabled(ChannelId.REVIEWS)
        } else {
            settingsRepo.getReviewsNotificationEnabled()
        }
    }

    // endregion
}
