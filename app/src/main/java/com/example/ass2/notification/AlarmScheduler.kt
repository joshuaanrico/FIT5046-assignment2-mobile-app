package com.example.ass2.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.content.getSystemService
import com.example.ass2.data.local.Reminder
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId

private const val TAG = "AlarmScheduler"

const val EXTRA_REMINDER_ID = "reminder_id"
const val EXTRA_REMINDER_LABEL = "reminder_label"
const val EXTRA_REPEAT_MASK = "repeat_mask"

class AlarmScheduler(private val context: Context) {

    private val am: AlarmManager? = context.getSystemService()

    // Registers an exact one-shot alarm with the system for the next matching day/time
    fun schedule(reminder: Reminder) {
        val am = am ?: return
        // Android 12+ requires exact alarm permission - silently skip if user revoked it
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !am.canScheduleExactAlarms()) {
            Log.w(TAG, "exact alarm permission missing - skip schedule for ${reminder.id}")
            return
        }
        val triggerAt = nextTriggerMillis(reminder) ?: return
        val pi = pendingIntent(reminder, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        // setExactAndAllowWhileIdle fires even in Doze mode - critical for reliable reminders
        am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAt, pi)
        Log.d(TAG, "scheduled id=${reminder.id} at=$triggerAt")
    }

    // Rebuilds the same PendingIntent used during schedule() - AlarmManager matches by request code + intent
    fun cancel(reminderId: Long) {
        val am = am ?: return
        val pi = PendingIntent.getBroadcast(
            context,
            reminderId.toInt(),
            Intent(context, ReminderReceiver::class.java).apply {
                putExtra(EXTRA_REMINDER_ID, reminderId)
            },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        am.cancel(pi)
        pi.cancel()  // releases the PendingIntent slot in the system
        Log.d(TAG, "cancelled id=$reminderId")
    }

    // Called by BootReceiver after device reboot - system wipes all alarms on power-off
    fun rescheduleAll(list: List<Reminder>) {
        list.filter { it.enabled }.forEach { schedule(it) }
    }

    // PendingIntent: pre-packaged Intent delivered by AlarmManager when alarm fires.
    // Reminder ID as request code ensures one PendingIntent per reminder (no overwrites).
    private fun pendingIntent(reminder: Reminder, flags: Int): PendingIntent {
        val intent = Intent(context, ReminderReceiver::class.java).apply {
            putExtra(EXTRA_REMINDER_ID, reminder.id)
            putExtra(EXTRA_REMINDER_LABEL, reminder.label)
            putExtra(EXTRA_REPEAT_MASK, reminder.repeatDaysMask)
        }
        return PendingIntent.getBroadcast(context, reminder.id.toInt(), intent, flags)
    }

    companion object {
        // Calculates the next future epoch millis the reminder should fire.
        // Searches up to 7 days ahead to find the next matching weekday in the bitmask.
        fun nextTriggerMillis(reminder: Reminder, from: LocalDateTime = LocalDateTime.now()): Long? {
            val time = LocalTime.of(reminder.hour, reminder.minute)
            val mask = reminder.repeatDaysMask
            // No repeat days set -> fire at the next occurrence (today if future, else tomorrow)
            if (mask == 0) {
                val today = from.toLocalDate().atTime(time)
                val target = if (today.isAfter(from)) today else today.plusDays(1)
                return target.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
            }
            for (offset in 0..7) {
                val candidate = from.toLocalDate().plusDays(offset.toLong()).atTime(time)
                if (offset == 0 && !candidate.isAfter(from)) continue
                if (matchesDay(mask, candidate.toLocalDate())) {
                    return candidate.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
                }
            }
            return null
        }

        // Each day = one bit. AND with mask returns non-zero only if that day's bit is set.
        private fun matchesDay(mask: Int, date: LocalDate): Boolean {
            val bit = when (date.dayOfWeek) {
                DayOfWeek.MONDAY -> 1
                DayOfWeek.TUESDAY -> 2
                DayOfWeek.WEDNESDAY -> 4
                DayOfWeek.THURSDAY -> 8
                DayOfWeek.FRIDAY -> 16
                DayOfWeek.SATURDAY -> 32
                DayOfWeek.SUNDAY -> 64
            }
            return mask and bit != 0
        }
    }
}
