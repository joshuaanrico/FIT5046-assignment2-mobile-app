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

                val finalExercises = if (selectedExercises.size == 4) {
                    selectedExercises
                } else {
                    mergeWithFallback(selectedExercises)
                }

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
                val fallback = fallbackPoseExercises()

                exerciseRepository.clear()
                exerciseRepository.insertAll(fallback)

                _uiState.update {
                    it.copy(
                        exercises = fallback,
                        isLoading = false,
                        errorMessage = null
                    )
                }
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
            category = category?.name ?: categoryForPose(appDisplayName),
            instructions = apiDescription
                .replace(Regex("<.*?>"), "")
                .ifBlank { fallbackInstructions(appDisplayName) },
            imageUrl = images.firstOrNull()?.image
        )
    }

    private fun mergeWithFallback(apiExercises: List<Exercise>): List<Exercise> {
        val fallback = fallbackPoseExercises()
        val existingNames = apiExercises.map { it.name }.toSet()
        val missing = fallback.filterNot { it.name in existingNames }

        return (apiExercises + missing)
            .distinctBy { it.name }
            .sortedBy { exerciseOrder(it.name) }
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
            "Bicep Curl" -> "Arms"
            "Side Stretch" -> "Core"
            "Forward Fold" -> "Legs"
            "Seated Twist" -> "Core"
            else -> "Mobility"
        }
    }

    private fun fallbackInstructions(name: String): String {
        return when (name) {
            "Bicep Curl" ->
                "Sit or stand upright. Keep your elbow close to your body, curl your forearm upward, then lower slowly with control."

            "Side Stretch" ->
                "Sit or stand tall. Raise one arm overhead and gently lean to the opposite side. Hold briefly, then switch sides."

            "Forward Fold" ->
                "Sit or stand with control. Slowly hinge forward from the hips, keeping the movement gentle and avoiding strain."

            "Seated Twist" ->
                "Sit upright. Rotate your torso gently to one side while keeping your hips stable, then repeat on the other side."

            else ->
                "Move slowly and stop if you feel discomfort."
        }
    }

    private fun fallbackPoseExercises(): List<Exercise> {
        return listOf(
            Exercise(
                id = "fallback_bicep_curl",
                name = "Bicep Curl",
                category = "Arms",
                instructions = fallbackInstructions("Bicep Curl"),
                imageUrl = null
            ),
            Exercise(
                id = "fallback_side_stretch",
                name = "Side Stretch",
                category = "Core",
                instructions = fallbackInstructions("Side Stretch"),
                imageUrl = null
            ),
            Exercise(
                id = "fallback_forward_fold",
                name = "Forward Fold",
                category = "Legs",
                instructions = fallbackInstructions("Forward Fold"),
                imageUrl = null
            ),
            Exercise(
                id = "fallback_seated_twist",
                name = "Seated Twist",
                category = "Core",
                instructions = fallbackInstructions("Seated Twist"),
                imageUrl = null
            )
        )
    }
}