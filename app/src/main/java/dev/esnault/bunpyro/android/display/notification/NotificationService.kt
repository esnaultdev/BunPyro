package dev.esnault.bunpyro.android.display.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import dev.esnault.bunpyro.R
import dev.esnault.bunpyro.android.MainActivity
import dev.esnault.bunpyro.common.getThemeColor


class NotificationService(
    private val context: Context
) : INotificationService {

    private val notificationManager =
        context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager

    init {
        createChannels()
    }

    // region Channels

    object ChannelId {
        const val SYNC = "sync"
    }

    private fun createChannels() {
        if (Build.VERSION.SDK_INT >= 26) {
            createChannel(
                channelId = ChannelId.SYNC,
                nameResId = R.string.notificationChannel_sync_name,
                descriptionResId = R.string.notificationChannel_sync_description,
                importance = NotificationManager.IMPORTANCE_LOW
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

    // endregion

    // region Notification

    object NotificationId {
        const val SYNC = 1
    }

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
}
