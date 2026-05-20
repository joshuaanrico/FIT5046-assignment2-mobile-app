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

    // Triggered by AlarmManager when scheduled alarm fires
    override fun onReceive(context: Context, intent: Intent) {
        val id = intent.getLongExtra(EXTRA_REMINDER_ID, -1L)
        val label = intent.getStringExtra(EXTRA_REMINDER_LABEL) ?: "Reminder"
        val repeatMask = intent.getIntExtra(EXTRA_REPEAT_MASK, 0)
        if (id < 0) return  // malformed intent - bail out

        ensureReminderChannel(context)
        showNotification(context, id, label)

        // Repeating reminders self-rearm - AlarmManager only fires one-shot alarms
        if (repeatMask != 0) rearmNext(context, id, label, repeatMask)
    }

    private fun showNotification(context: Context, id: Long, label: String) {
        val prefs = context.getSharedPreferences("sitfit_settings", Context.MODE_PRIVATE)

        val pushEnabled = prefs.getBoolean("push_notifications", true)
        val soundVibrationEnabled = prefs.getBoolean("sound_vibration", true)

        if (!pushEnabled) return
        // Android 13+ requires runtime permission - silently skip if user denied notifications
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


        if (soundVibrationEnabled) {
            notif.setDefaults(
                NotificationCompat.DEFAULT_SOUND or NotificationCompat.DEFAULT_VIBRATE
            )
        } else {
            notif.setSilent(true)
        }

        NotificationManagerCompat.from(context).notify(id.toInt(), notif.build())
    }

    // Reschedules the alarm for the next matching day.
    // goAsync() extends the receiver's lifetime so coroutine DB call can complete (default 10s).
    // Reads fresh state from DB in case user disabled or edited the reminder before firing.
    private fun rearmNext(context: Context, id: Long, label: String, repeatMask: Int) {
        val pending = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val dao = AppDatabase.getDatabase(context).reminderDao()
                val current: Reminder? = dao.getById(id).firstOrNull()
                // Fallback to intent data if DB row is missing (edge case: user deleted between fire + rearm)
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
                pending.finish()  // tells the system the receiver is done - releases process
            }
        }
    }
}
