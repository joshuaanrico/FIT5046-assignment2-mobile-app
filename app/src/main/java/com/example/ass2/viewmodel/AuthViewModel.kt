package com.example.ass2.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import android.util.Patterns
const val WEB_CLIENT_ID = "1017626561698-63hggmjkld746mgrcm8mjo1lkdio9sgp.apps.googleusercontent.com"

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    object Success : AuthState()
    data class Error(val message: String) : AuthState()
}

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val auth: FirebaseAuth = Firebase.auth

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState

    fun signInWithEmail(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _authState.value = AuthState.Error("Email and password cannot be empty")
            return
        }
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                auth.signInWithEmailAndPassword(email, password).await()
                _authState.value = AuthState.Success
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Sign in failed")
            }
        }
    }

    fun registerWithEmail(email: String, password: String, confirmPassword: String) {
        if (email.isBlank() || password.isBlank()) {
            _authState.value = AuthState.Error("All fields are required")
            return
        }
        if (password != confirmPassword) {
            _authState.value = AuthState.Error("Passwords do not match")
            return
        }
        if (password.length < 8) {
            _authState.value = AuthState.Error("Password must be at least 8 characters")
            return
        }
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                auth.createUserWithEmailAndPassword(email, password).await()
                _authState.value = AuthState.Success
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Registration failed")
            }
        }
    }

    fun sendPasswordReset(email: String) {
        val trimmedEmail = email.trim()

        if (trimmedEmail.isBlank()) {
            _authState.value = AuthState.Error("Please enter your email address")
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(trimmedEmail).matches()) {
            _authState.value = AuthState.Error("Please enter a valid email address")
            return
        }

        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                auth.sendPasswordResetEmail(trimmedEmail).await()
                _authState.value = AuthState.Success
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Failed to send reset email")
            }
        }
    }

    // Called from the screen after the Google Sign-In intent returns a result
    fun handleGoogleSignInResult(task: Task<GoogleSignInAccount>) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val account = task.getResult(ApiException::class.java)
                val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                auth.signInWithCredential(credential).await()
                _authState.value = AuthState.Success
            } catch (e: ApiException) {
                _authState.value = AuthState.Error("Google Sign-In failed (code ${e.statusCode})")
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Google Sign-In failed")
            }
        }
    }

    fun signOut() {
        auth.signOut()
    }

    fun resetState() {
        _authState.value = AuthState.Idle
    }

    fun isUserLoggedIn(): Boolean = auth.currentUser != null
}
