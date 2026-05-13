package com.example.ass2.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ass2.data.local.Profile
import com.example.ass2.data.repository.ProfileRepository
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

// ViewModel responsible for loading and saving the user's profile from Room.
// Bridges ProfileScreen and ProfileSetupScreen with the local Room database.
class ProfileViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = ProfileRepository(application)

    // Firebase Auth instance used to get the current user's UID and email
    private val auth = Firebase.auth

    // The Firebase UID used as the primary key in the Room profile table
    val uid: String get() = auth.currentUser?.uid ?: ""

    // Email address from Firebase Auth — not stored in Room since Firebase is the source of truth
    val email: String get() = auth.currentUser?.email ?: ""

    // Observes the profile row for the logged-in user from Room.
    // Emits null if the user has not completed profile setup yet.
    // WhileSubscribed(5000) keeps the upstream Flow alive for 5 seconds after
    // the last collector disappears, avoiding unnecessary DB re-queries on recomposition.
    val profile: StateFlow<Profile?> = repository.observe(uid)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    // Persists the profile to Room on a background thread.
    // Uses REPLACE conflict strategy (upsert) so this works for both
    // first-time setup and subsequent edits.
    fun saveProfile(profile: Profile) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.upsert(profile)
        }
    }
}
