package com.example.ass2.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ass2.data.local.Reminder
import com.example.ass2.data.repository.ReminderRepository
import com.example.ass2.notification.AlarmScheduler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ReminderViewModel(application: Application) : AndroidViewModel(application) {
    private val repo = ReminderRepository(application)
    private val scheduler = AlarmScheduler(application)

    // Hot StateFlow shared across all collectors — 5s delay keeps DB query alive during screen rotation
    val reminders: StateFlow<List<Reminder>> = repo.all
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun byId(id: Long): Flow<Reminder?> = repo.byId(id)

    // Insert if new (id==0), update if existing — then sync the alarm to match the saved state
    fun upsert(r: Reminder) = viewModelScope.launch(Dispatchers.IO) {
        val saved = if (r.id == 0L) {
            val newId = repo.insert(r)
            r.copy(id = newId)  // copy generated ID back so scheduler can reference it
        } else {
            repo.update(r)
            r
        }
        if (saved.enabled) scheduler.schedule(saved) else scheduler.cancel(saved.id)
    }

    // Flips the enabled flag without opening the edit screen — used by the toggle switch in the list
    fun toggleEnabled(r: Reminder, enabled: Boolean) = viewModelScope.launch(Dispatchers.IO) {
        val updated = r.copy(enabled = enabled)
        repo.update(updated)
        if (enabled) scheduler.schedule(updated) else scheduler.cancel(updated.id)
    }

    // Cancel alarm before deleting — order matters since the ID is gone after repo.delete
    fun delete(r: Reminder) = viewModelScope.launch(Dispatchers.IO) {
        scheduler.cancel(r.id)
        repo.delete(r)
    }
}
