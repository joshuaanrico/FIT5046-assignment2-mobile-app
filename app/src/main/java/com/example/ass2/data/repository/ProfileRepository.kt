package com.example.ass2.data.repository

import android.app.Application
import com.example.ass2.data.local.AppDatabase
import com.example.ass2.data.local.Profile

class ProfileRepository(application: Application) {
    private val dao = AppDatabase.getDatabase(application).profileDao()

    fun observe(uid: String) = dao.observe(uid)

    suspend fun upsert(profile: Profile) = dao.upsert(profile)
    suspend fun delete(uid: String) = dao.delete(uid)
}
