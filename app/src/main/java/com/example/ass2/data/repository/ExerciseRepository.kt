package com.example.ass2.data.repository

import android.app.Application
import com.example.ass2.data.local.AppDatabase
import com.example.ass2.data.local.Exercise

class ExerciseRepository(application: Application) {
    private val dao = AppDatabase.getDatabase(application).exerciseDao()

    val all = dao.getAll()

    fun byCategory(cat: String) = dao.getByCategory(cat)
    fun byId(id: String) = dao.getById(id)

    suspend fun insertAll(items: List<Exercise>) = dao.insertAll(items)
    suspend fun clear() = dao.clear()
}
