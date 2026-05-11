package com.example.ass2.data.repository

import android.app.Application
import com.example.ass2.data.local.AppDatabase
import com.example.ass2.data.local.Session
import kotlinx.coroutines.flow.Flow

// Thin wrapper over SessionDao - separates data access from ViewModel logic
class SessionRepository(application: Application) {
    private val dao = AppDatabase.getDatabase(application).sessionDao()

    val all: Flow<List<Session>> = dao.getAll()

    fun byExercise(name: String) = dao.getByExercise(name)
    fun byId(id: Long) = dao.getById(id)
    fun since(sinceMillis: Long) = dao.getSince(sinceMillis)
    fun countSince(sinceMillis: Long) = dao.countSince(sinceMillis)
    fun totalDurationSince(sinceMillis: Long) = dao.totalDurationSince(sinceMillis)

    suspend fun insert(session: Session) = dao.insert(session)
    suspend fun update(session: Session) = dao.update(session)
    suspend fun delete(session: Session) = dao.delete(session)
}
