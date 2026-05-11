package com.example.ass2.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ass2.data.local.Session
import com.example.ass2.data.repository.SessionRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SessionViewModel(application: Application) : AndroidViewModel(application) {
    private val repo = SessionRepository(application)

    // Hot StateFlow shared across all collectors — 5s delay keeps DB query alive during screen rotation
    val sessions: StateFlow<List<Session>> = repo.all
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun byId(id: Long): Flow<Session?> = repo.byId(id)
    // Time-filtered queries used by ProgressScreen to calculate stats per period (week/month)
    fun since(sinceMillis: Long): Flow<List<Session>> = repo.since(sinceMillis)
    fun countSince(sinceMillis: Long): Flow<Int> = repo.countSince(sinceMillis)
    fun totalDurationSince(sinceMillis: Long): Flow<Int> = repo.totalDurationSince(sinceMillis)

    fun insert(s: Session) = viewModelScope.launch(Dispatchers.IO) { repo.insert(s) }
    fun update(s: Session) = viewModelScope.launch(Dispatchers.IO) { repo.update(s) }
    fun delete(s: Session) = viewModelScope.launch(Dispatchers.IO) { repo.delete(s) }
}
