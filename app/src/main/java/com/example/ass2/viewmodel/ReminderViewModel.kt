package com.example.ass2.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ass2.data.local.Reminder
import com.example.ass2.data.repository.ReminderRepository
import com.example.ass2.notification.AlarmScheduler
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
class ReminderViewModel(application: Application) : AndroidViewModel(application) {
    private val repo = ReminderRepository(application)
    private val scheduler = AlarmScheduler(application)
    private val auth = FirebaseAuth.getInstance()

    // Tracks the signed-in Firebase uid; flips on login/logout so the reminders flow resubscribes
    private val currentUid = MutableStateFlow(auth.currentUser?.uid ?: "")
    private val authListener = FirebaseAuth.AuthStateListener { fa ->
        currentUid.value = fa.currentUser?.uid ?: ""
    }

    init {
        auth.addAuthStateListener(authListener)
    }

    // Hot StateFlow shared across all collectors - flatMapLatest re-queries when uid changes (account switch)
    val reminders: StateFlow<List<Reminder>> = currentUid
        .flatMapLatest { uid -> if (uid.isEmpty()) flowOf(emptyList()) else repo.all(uid) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun byId(id: Long): Flow<Reminder?> = currentUid
        .flatMapLatest { uid -> if (uid.isEmpty()) flowOf(null) else repo.byId(uid, id) }

    // Insert if new (id==0), update if existing - then sync the alarm to match the saved state.
    // Stamps the current uid so a reminder created by user A never appears in user B's list.
    fun upsert(r: Reminder) = viewModelScope.launch(Dispatchers.IO) {
        val uid = currentUid.value
        if (uid.isEmpty()) return@launch
        val stamped = r.copy(userId = uid)
        val saved = if (stamped.id == 0L) {
            val newId = repo.insert(stamped)
            stamped.copy(id = newId)  // copy generated ID back so scheduler can reference it
        } else {
            repo.update(stamped)
            stamped
        }
        if (saved.enabled) scheduler.schedule(saved) else scheduler.cancel(saved.id)
    }

    // Flips the enabled flag without opening the edit screen - used by the toggle switch in the list
    fun toggleEnabled(r: Reminder, enabled: Boolean) = viewModelScope.launch(Dispatchers.IO) {
        val updated = r.copy(enabled = enabled)
        repo.update(updated)
        if (enabled) scheduler.schedule(updated) else scheduler.cancel(updated.id)
    }

    // Cancel alarm before deleting - order matters since the ID is gone after repo.delete
    fun delete(r: Reminder) = viewModelScope.launch(Dispatchers.IO) {
        scheduler.cancel(r.id)
        repo.delete(r)
    }

    override fun onCleared() {
        auth.removeAuthStateListener(authListener)
        super.onCleared()
    }
}
