package com.example.ass2.data.repository

import android.app.Application
import com.example.ass2.data.local.AppDatabase
import com.example.ass2.data.local.Session
import kotlinx.coroutines.flow.Flow

// Thin wrapper over SessionDao - separates data access from ViewModel logic.
// All read queries take userId so rows from a previous account never leak into the current session.
class SessionRepository(application: Application) {
    private val dao = AppDatabase.getDatabase(application).sessionDao()

    fun all(userId: String): Flow<List<Session>> = dao.getAll(userId)

    fun byExercise(userId: String, name: String) = dao.getByExercise(userId, name)
    fun byId(userId: String, id: Long) = dao.getById(userId, id)
    fun since(userId: String, sinceMillis: Long) = dao.getSince(userId, sinceMillis)
    fun countSince(userId: String, sinceMillis: Long) = dao.countSince(userId, sinceMillis)
    fun totalDurationSince(userId: String, sinceMillis: Long) = dao.totalDurationSince(userId, sinceMillis)

    suspend fun insert(session: Session) = dao.insert(session)
    suspend fun update(session: Session) = dao.update(session)
    suspend fun delete(session: Session) = dao.delete(session)
    suspend fun deleteAllForUser(userId: String) = dao.deleteAllForUser(userId)
}
