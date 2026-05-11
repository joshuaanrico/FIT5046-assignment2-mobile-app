package com.example.ass2.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.content.getSystemService

const val REMINDER_CHANNEL_ID = "sitfit-reminders"
private const val REMINDER_CHANNEL_NAME = "Reminders"
private const val REMINDER_CHANNEL_DESC = "Scheduled exercise + wellness reminders"

// Channels are required on Android 8+ for any notification - users can mute the channel in Settings
fun ensureReminderChannel(context: Context) {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return  // pre-O: no channels needed
    val nm = context.getSystemService<NotificationManager>() ?: return
    if (nm.getNotificationChannel(REMINDER_CHANNEL_ID) != null) return  // idempotent - skip if already created
    val channel = NotificationChannel(
        REMINDER_CHANNEL_ID,
        REMINDER_CHANNEL_NAME,
        NotificationManager.IMPORTANCE_HIGH
    ).apply {
        description = REMINDER_CHANNEL_DESC
        enableVibration(true)
    }
    nm.createNotificationChannel(channel)
}
