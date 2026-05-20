package com.example.ass2.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class SettingsUiState(
    val pushNotifications: Boolean = true,
    val soundVibration: Boolean = true,
    val highContrastMode: Boolean = false,
    val largerText: Boolean = false
)

class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val prefs = application.getSharedPreferences(
        "sitfit_settings",
        Application.MODE_PRIVATE
    )

    private val _uiState = MutableStateFlow(
        SettingsUiState(
            pushNotifications = prefs.getBoolean("push_notifications", true),
            soundVibration = prefs.getBoolean("sound_vibration", true),
            highContrastMode = prefs.getBoolean("high_contrast_mode", false),
            largerText = prefs.getBoolean("larger_text", false)
        )
    )



    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    fun setPushNotifications(value: Boolean) {
        prefs.edit().putBoolean("push_notifications", value).apply()
        _uiState.update { it.copy(pushNotifications = value) }
    }

    fun setSoundVibration(value: Boolean) {
        prefs.edit().putBoolean("sound_vibration", value).apply()
        _uiState.update { it.copy(soundVibration = value) }
    }

    fun setHighContrastMode(value: Boolean) {
        prefs.edit().putBoolean("high_contrast_mode", value).apply()
        _uiState.update { it.copy(highContrastMode = value) }
    }

    fun setLargerText(value: Boolean) {
        prefs.edit().putBoolean("larger_text", value).apply()
        _uiState.update { it.copy(largerText = value) }
    }
}
