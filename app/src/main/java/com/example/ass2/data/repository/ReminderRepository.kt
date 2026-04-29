package com.example.ass2.data.repository

import android.app.Application
import com.example.ass2.data.local.AppDatabase
import com.example.ass2.data.local.Reminder
import kotlinx.coroutines.flow.Flow

class ReminderRepository(application: Application) {
    private val dao = AppDatabase.getDatabase(application).reminderDao()

    val all: Flow<List<Reminder>> = dao.getAll()

    fun byId(id: Long) = dao.getById(id)
    fun getEnabled() = dao.getEnabled()

    suspend fun insert(reminder: Reminder) = dao.insert(reminder)
    suspend fun update(reminder: Reminder) = dao.update(reminder)
    suspend fun delete(reminder: Reminder) = dao.delete(reminder)
}
