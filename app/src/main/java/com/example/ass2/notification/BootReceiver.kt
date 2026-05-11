package com.example.ass2.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.ass2.data.local.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

// Restores all enabled reminders after device reboot - Android wipes alarms on power-off.
// goAsync() extends the receiver lifetime so the DB read + reschedule loop can finish.
class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED) return  // guard against unrelated broadcasts
        val pending = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val list = AppDatabase.getDatabase(context).reminderDao().getEnabled().firstOrNull().orEmpty()
                AlarmScheduler(context).rescheduleAll(list)
            } finally {
                pending.finish()
            }
        }
    }
}
