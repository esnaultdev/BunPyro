package dev.esnault.bunpyro.android.display.notification

import android.app.Notification


data class NotificationWithId(
    val id: Int,
    val notification: Notification
)

fun Notification.withId(id: Int) = NotificationWithId(id, this)
