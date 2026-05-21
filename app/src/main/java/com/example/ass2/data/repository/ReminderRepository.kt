package com.example.ass2.data.repository

import android.app.Application
import com.example.ass2.data.local.AppDatabase
import com.example.ass2.data.local.Reminder
import kotlinx.coroutines.flow.Flow

// Thin wrapper over ReminderDao - separates data access from ViewModel logic.
// All per-user read queries take userId so reminders from a previous account never leak.
class ReminderRepository(application: Application) {
    private val dao = AppDatabase.getDatabase(application).reminderDao()

    fun all(userId: String): Flow<List<Reminder>> = dao.getAll(userId)

    fun byId(userId: String, id: Long) = dao.getById(userId, id)

    // BootReceiver path stays user-agnostic on purpose - phone reboots before login, no current user yet
    fun getEnabled() = dao.getEnabled()

    suspend fun insert(reminder: Reminder) = dao.insert(reminder)
    suspend fun update(reminder: Reminder) = dao.update(reminder)
    suspend fun delete(reminder: Reminder) = dao.delete(reminder)
    suspend fun listForUser(userId: String): List<Reminder> = dao.listForUser(userId)
}
