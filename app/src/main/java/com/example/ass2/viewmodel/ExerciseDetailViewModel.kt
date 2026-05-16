package com.example.ass2.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ass2.data.local.Exercise
import com.example.ass2.data.repository.ExerciseRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ExerciseDetailUiState(
    val exercise: Exercise? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class ExerciseDetailViewModel(application: Application) : AndroidViewModel(application) {

    private val exerciseRepository = ExerciseRepository(application)

    private val _uiState = MutableStateFlow(ExerciseDetailUiState())
    val uiState: StateFlow<ExerciseDetailUiState> = _uiState.asStateFlow()

    fun loadExercise(exerciseName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.update {
                it.copy(isLoading = true, errorMessage = null)
            }

            try {
                val exercises = exerciseRepository.all.first()

                val selectedExercise = exercises.firstOrNull {
                    it.name.equals(exerciseName, ignoreCase = true)
                }

                _uiState.update {
                    it.copy(
                        exercise = selectedExercise,
                        isLoading = false,
                        errorMessage = if (selectedExercise == null) {
                            "Exercise details not found."
                        } else {
                            null
                        }
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Could not load exercise details."
                    )
                }
            }
        }
    }
}