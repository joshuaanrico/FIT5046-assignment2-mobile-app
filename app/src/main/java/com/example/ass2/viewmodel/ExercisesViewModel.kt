package com.example.ass2.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ass2.data.local.Exercise
import com.example.ass2.data.remote.ApiExerciseInfo
import com.example.ass2.data.remote.RetrofitClient
import com.example.ass2.data.repository.ExerciseRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ExercisesUiState(
    val exercises: List<Exercise> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class ExercisesViewModel(application: Application) : AndroidViewModel(application) {

    private val exerciseRepository = ExerciseRepository(application)

    private val _uiState = MutableStateFlow(ExercisesUiState())
    val uiState: StateFlow<ExercisesUiState> = _uiState.asStateFlow()

    private val supportedExerciseMap = mapOf(
        1192 to "Bicep Curl",
        1650 to "Side Stretch",
        1652 to "Forward Fold",
        1089 to "Seated Twist"
    )

    init {
        loadExercises()
    }

    private fun loadExercises() {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.update {
                it.copy(isLoading = true, errorMessage = null)
            }

            try {
                val cachedExercises = exerciseRepository.all.first()

                if (cachedExercises.isNotEmpty()) {
                    _uiState.update {
                        it.copy(
                            exercises = cachedExercises,
                            isLoading = false,
                            errorMessage = null
                        )
                    }
                }

                val response = RetrofitClient.apiService.getExerciseInfoList()

                val selectedExercises = response.results
                    .filter { item -> supportedExerciseMap.containsKey(item.id) }
                    .map { item ->
                        item.toRoomExercise(
                            appDisplayName = supportedExerciseMap[item.id] ?: "Exercise"
                        )
                    }
                    .sortedBy { exerciseOrder(it.name) }

                val finalExercises = selectedExercises

                exerciseRepository.clear()
                exerciseRepository.insertAll(finalExercises)

                _uiState.update {
                    it.copy(
                        exercises = finalExercises,
                        isLoading = false,
                        errorMessage = null
                    )
                }
            } catch (e: Exception) {
                val fallback = ""

                exerciseRepository.clear()
            }
        }
    }

    private fun ApiExerciseInfo.toRoomExercise(appDisplayName: String): Exercise {
        val english = translations.firstOrNull { it.language == 2 }
            ?: translations.firstOrNull()

        val apiDescription = english?.description_source
            ?: english?.description
            ?: ""

        return Exercise(
            id = id.toString(),
            name = appDisplayName,
            category = categoryForPose(appDisplayName),
            instructions = apiDescription
                .replace(Regex("<.*?>"), "")
                .ifBlank { "" },
            imageUrl = images.firstOrNull()?.image
        )
    }



    private fun exerciseOrder(name: String): Int {
        return when (name) {
            "Bicep Curl" -> 1
            "Side Stretch" -> 2
            "Forward Fold" -> 3
            "Seated Twist" -> 4
            else -> 99
        }
    }

    private fun categoryForPose(name: String): String {
        return when (name) {
            "Bicep Curl" -> "Strength"
            "Side Stretch" -> "Stretch"
            "Forward Fold" -> "Mobility"
            "Seated Twist" -> "Core"
            else -> "Mobility"
        }
    }



}