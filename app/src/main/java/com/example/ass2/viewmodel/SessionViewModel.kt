package com.example.ass2.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ass2.data.local.Session
import com.example.ass2.data.repository.SessionRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class SessionViewModel(application: Application) : AndroidViewModel(application) {
    private val repo = SessionRepository(application)
    private val auth = FirebaseAuth.getInstance()

    // Tracks the signed-in Firebase uid; flips on login/logout so all dependent flows resubscribe
    private val currentUid = MutableStateFlow(auth.currentUser?.uid ?: "")
    private val authListener = FirebaseAuth.AuthStateListener { fa ->
        currentUid.value = fa.currentUser?.uid ?: ""
    }

    init {
        auth.addAuthStateListener(authListener)
    }

    // Hot StateFlow shared across all collectors - flatMapLatest re-queries when uid changes (account switch)
    val sessions: StateFlow<List<Session>> = currentUid
        .flatMapLatest { uid -> if (uid.isEmpty()) flowOf(emptyList()) else repo.all(uid) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun byId(id: Long): Flow<Session?> = currentUid
        .flatMapLatest { uid -> if (uid.isEmpty()) flowOf(null) else repo.byId(uid, id) }

    // Time-filtered queries used by ProgressScreen to calculate stats per period (week/month)
    fun since(sinceMillis: Long): Flow<List<Session>> = currentUid
        .flatMapLatest { uid -> if (uid.isEmpty()) flowOf(emptyList()) else repo.since(uid, sinceMillis) }

    fun countSince(sinceMillis: Long): Flow<Int> = currentUid
        .flatMapLatest { uid -> if (uid.isEmpty()) flowOf(0) else repo.countSince(uid, sinceMillis) }

    fun totalDurationSince(sinceMillis: Long): Flow<Int> = currentUid
        .flatMapLatest { uid -> if (uid.isEmpty()) flowOf(0) else repo.totalDurationSince(uid, sinceMillis) }

    // Stamp the current uid on insert so callers don't need to know about auth at the UI layer
    fun insert(s: Session) = viewModelScope.launch(Dispatchers.IO) {
        val uid = currentUid.value
        if (uid.isEmpty()) return@launch
        repo.insert(s.copy(userId = uid))
    }
    fun update(s: Session) = viewModelScope.launch(Dispatchers.IO) { repo.update(s) }
    fun delete(s: Session) = viewModelScope.launch(Dispatchers.IO) { repo.delete(s) }

    override fun onCleared() {
        auth.removeAuthStateListener(authListener)
        super.onCleared()
    }
}
