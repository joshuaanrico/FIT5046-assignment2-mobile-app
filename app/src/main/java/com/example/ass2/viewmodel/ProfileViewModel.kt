package com.example.ass2.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ass2.data.local.Profile
import com.example.ass2.data.repository.ProfileRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ProfileUiState(
    val userId: String = "",
    val displayName: String = "User",
    val email: String = "",
    val dob: Long? = null,
    val heightCm: Float? = null,
    val weightKg: Float? = null,
    val conditions: String = "Not set",
    val painBaseline: Int? = null,
    val isProfileSetup: Boolean = false,
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)

class ProfileViewModel(application: Application) : AndroidViewModel(application) {

    private val auth = FirebaseAuth.getInstance()
    private val profileRepository = ProfileRepository(application)

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadProfile()
    }

    private fun loadProfile() {
        val user = auth.currentUser

        if (user == null) {
            _uiState.update {
                it.copy(
                    displayName = "Guest",
                    email = "",
                    isLoading = false,
                    errorMessage = "No signed-in user found."
                )
            }
            return
        }

        val uid = user.uid
        val fallbackName = user.displayName
            ?: user.email?.substringBefore("@")
            ?: "User"

        _uiState.update {
            it.copy(
                userId = uid,
                displayName = fallbackName,
                email = user.email.orEmpty(),
                isLoading = true,
                errorMessage = null
            )
        }

        viewModelScope.launch {
            profileRepository.observe(uid).collect { profile ->
                if (profile == null) {
                    _uiState.update {
                        it.copy(
                            userId = uid,
                            displayName = fallbackName,
                            email = user.email.orEmpty(),
                            conditions = "Not set",
                            painBaseline = null,
                            isProfileSetup = false,
                            isLoading = false
                        )
                    }
                } else {
                    _uiState.update {
                        it.copy(
                            userId = uid,
                            displayName = profile.displayName.ifBlank { fallbackName },
                            email = user.email.orEmpty(),
                            dob = profile.dob,
                            heightCm = profile.heightCm,
                            weightKg = profile.weightKg,
                            conditions = profile.conditions?.ifBlank { "Not set" } ?: "Not set",
                            painBaseline = profile.painBaseline,
                            isProfileSetup = true,
                            isLoading = false,
                            errorMessage = null
                        )
                    }
                }
            }
        }
    }

    fun saveProfile(
        displayName: String,
        dob: Long?,
        heightCm: Float?,
        weightKg: Float?,
        conditions: String?,
        painBaseline: Int?
    ) {
        val user = auth.currentUser ?: return

        viewModelScope.launch {
            val profile = Profile(
                userId = user.uid,
                displayName = displayName.ifBlank {
                    user.displayName ?: user.email?.substringBefore("@") ?: "User"
                },
                dob = dob,
                heightCm = heightCm,
                weightKg = weightKg,
                conditions = conditions,
                painBaseline = painBaseline
            )

            profileRepository.upsert(profile)
        }
    }

    fun signOut() {
        auth.signOut()
    }
}