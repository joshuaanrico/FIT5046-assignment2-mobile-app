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
import com.example.ass2.data.repository.ReminderRepository
import com.example.ass2.notification.AlarmScheduler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import android.util.Patterns

// OAuth 2.0 Web Client ID from the Firebase project, used to authenticate Google Sign-In tokens
const val WEB_CLIENT_ID = "1017626561698-63hggmjkld746mgrcm8mjo1lkdio9sgp.apps.googleusercontent.com"

// Represents the possible states of an authentication operation
sealed class AuthState {
    object Idle : AuthState()       // No operation in progress
    object Loading : AuthState()    // Waiting for Firebase response
    object Success : AuthState()    // Operation completed successfully
    data class Error(val message: String) : AuthState() // Operation failed with an error message
}

// ViewModel that handles all authentication logic for login, registration,
// password reset, and Google Sign-In using Firebase Authentication.
// Uses AndroidViewModel to access application context for SharedPreferences.
class AuthViewModel(application: Application) : AndroidViewModel(application) {

    // Firebase Authentication instance for performing auth operations
    private val auth: FirebaseAuth = Firebase.auth

    // Exposes auth state to the UI — screens observe this to react to login/register results
    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState

    // SharedPreferences used to persist the user's email when "Remember Me" is checked
    private val prefs = application.getSharedPreferences("auth_prefs", android.content.Context.MODE_PRIVATE)

    // Returns the previously saved email, or empty string if none was saved
    fun getSavedEmail(): String = prefs.getString("saved_email", "") ?: ""

    // Signs in the user with email and password via Firebase.
    // If rememberMe is true, saves the email locally so it is pre-filled on next launch.
    fun signInWithEmail(email: String, password: String, rememberMe: Boolean) {
        if (email.isBlank() || password.isBlank()) {
            _authState.value = AuthState.Error("Email and password cannot be empty")
            return
        }
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                auth.signInWithEmailAndPassword(email, password).await()
                // Save or clear the email based on the Remember Me checkbox
                if (rememberMe) prefs.edit().putString("saved_email", email).apply()
                else prefs.edit().remove("saved_email").apply()
                _authState.value = AuthState.Success
            } catch (e: Exception) {
                _authState.value = AuthState.Error(friendlyError(e))
            }
        }
    }

    // Creates a new Firebase account after validating that:
    // - fields are not blank
    // - passwords match
    // - password meets strength requirements (min 8 chars, 1 uppercase, 1 special character)
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
        if (!password.any { it.isUpperCase() }) {
            _authState.value = AuthState.Error("Password must contain at least 1 uppercase letter")
            return
        }
        if (!password.any { it.isDigit() }) {
            _authState.value = AuthState.Error("Password must contain at least 1 number")
            return
        }
        if (!password.any { !it.isLetterOrDigit() }) {
            _authState.value = AuthState.Error("Password must contain at least 1 special character")
            return
        }
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                auth.createUserWithEmailAndPassword(email, password).await()
                _authState.value = AuthState.Success
            } catch (e: Exception) {
                _authState.value = AuthState.Error(friendlyError(e))
            }
        }
    }

    // Sends a password reset email via Firebase to the provided address.
    // Firebase only delivers the email if the address belongs to a registered account.
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
                _authState.value = AuthState.Error(friendlyError(e))
            }
        }
    }

    // Completes Google Sign-In by exchanging the Google ID token for a Firebase credential.
    // Called from the screen after the Google Sign-In intent returns a result.
    fun handleGoogleSignInResult(task: Task<GoogleSignInAccount>) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val account = task.getResult(ApiException::class.java)
                // Exchange the Google ID token for a Firebase auth credential
                val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                auth.signInWithCredential(credential).await()
                _authState.value = AuthState.Success
            } catch (e: ApiException) {
                _authState.value = AuthState.Error("Google sign-in failed. Please try again.")
            } catch (e: Exception) {
                _authState.value = AuthState.Error(friendlyError(e))
            }
        }
    }

    // Signs the current user out of Firebase.
    // Cancels any AlarmManager alarms belonging to this user first - otherwise the system would
    // keep firing notifications for someone who is no longer signed in (or for the next account).
    fun signOut() {
        val uid = auth.currentUser?.uid
        if (uid.isNullOrEmpty()) {
            auth.signOut()
            return
        }
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val repo = ReminderRepository(getApplication())
                val scheduler = AlarmScheduler(getApplication())
                repo.listForUser(uid).forEach { scheduler.cancel(it.id) }
            }
            auth.signOut()
        }
    }

    // Resets auth state back to Idle — called after the UI has handled a Success or Error
    fun resetState() {
        _authState.value = AuthState.Idle
    }

    // Returns true if a Firebase user session is currently active
    fun isUserLoggedIn(): Boolean = auth.currentUser != null

    // Converts raw Firebase exception messages into short, user-friendly strings
    private fun friendlyError(e: Exception): String {
        val msg = e.message?.lowercase() ?: ""
        return when {
            "credential is incorrect" in msg || "malformed" in msg || "expired" in msg
                || "invalid credential" in msg -> "Incorrect email or password. Please try again."
            "no user record" in msg || "user-not-found" in msg -> "No account found with that email."
            "email address is already in use" in msg || "email already in use" in msg -> "An account with this email already exists."
            "password is invalid" in msg || "wrong-password" in msg -> "Incorrect password. Please try again."
            "badly formatted" in msg || "invalid-email" in msg -> "Please enter a valid email address."
            "too many requests" in msg || "too-many-requests" in msg -> "Too many attempts. Please wait a moment and try again."
            "network" in msg -> "No internet connection. Please check your network."
            else -> "Something went wrong. Please try again."
        }
    }
}
