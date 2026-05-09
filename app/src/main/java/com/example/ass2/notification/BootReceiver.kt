package com.example.ass2.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.ass2.data.local.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED) return
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
