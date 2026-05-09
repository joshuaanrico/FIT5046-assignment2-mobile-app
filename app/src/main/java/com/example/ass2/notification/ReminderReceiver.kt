package com.example.ass2.notification

import android.Manifest
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.ass2.MainActivity
import com.example.ass2.R
import com.example.ass2.data.local.AppDatabase
import com.example.ass2.data.local.Reminder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class ReminderReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val id = intent.getLongExtra(EXTRA_REMINDER_ID, -1L)
        val label = intent.getStringExtra(EXTRA_REMINDER_LABEL) ?: "Reminder"
        val repeatMask = intent.getIntExtra(EXTRA_REPEAT_MASK, 0)
        if (id < 0) return

        ensureReminderChannel(context)
        showNotification(context, id, label)

        if (repeatMask != 0) rearmNext(context, id, label, repeatMask)
    }

    private fun showNotification(context: Context, id: Long, label: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED
        ) return

        val tapIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val tapPi = PendingIntent.getActivity(
            context, id.toInt(), tapIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notif = NotificationCompat.Builder(context, REMINDER_CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(label)
            .setContentText("Time for your $label session")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(tapPi)
            .build()

        NotificationManagerCompat.from(context).notify(id.toInt(), notif)
    }

    private fun rearmNext(context: Context, id: Long, label: String, repeatMask: Int) {
        val pending = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val dao = AppDatabase.getDatabase(context).reminderDao()
                val current: Reminder? = dao.getById(id).firstOrNull()
                val source = current ?: Reminder(
                    id = id,
                    label = label,
                    hour = 0,
                    minute = 0,
                    repeatDaysMask = repeatMask,
                    enabled = true
                )
                if (source.enabled && source.repeatDaysMask != 0) {
                    AlarmScheduler(context).schedule(source)
                }
            } finally {
                pending.finish()
            }
        }
    }
}
