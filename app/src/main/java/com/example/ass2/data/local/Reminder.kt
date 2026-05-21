package com.example.ass2.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reminders")
data class Reminder(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val userId: String = "",     // Firebase Auth uid - isolates rows per user (stamped by ReminderViewModel.upsert)
    val label: String,
    val hour: Int,               // 0..23
    val minute: Int,             // 0..59
    val repeatDaysMask: Int,     // bitmask: Mon=1, Tue=2, Wed=4, Thu=8, Fri=16, Sat=32, Sun=64
    val enabled: Boolean = true
)
