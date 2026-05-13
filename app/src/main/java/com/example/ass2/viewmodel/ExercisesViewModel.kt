package com.example.ass2.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ass2.data.local.Exercise
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

                val response = RetrofitClient.apiService.getExercises()

                val apiExercises = response.results
                    .filter { item ->
                        val name = item.name.orEmpty()
                        val description = item.description.orEmpty()
                        isSupportedPoseExercise(name, description)
                    }
                    .map { item ->
                        val name = item.name.orEmpty()
                        val description = item.description.orEmpty()

                        Exercise(
                            id = item.id.toString(),
                            name = normaliseExerciseName(name, description),
                            category = guessCategory(name, description),
                            instructions = description
                                .replace(Regex("<.*?>"), "")
                                .ifBlank { fallbackInstructions(normaliseExerciseName(name, description)) },
                            imageUrl = null
                        )
                    }
                    .distinctBy { it.name }

                val finalExercises = if (apiExercises.isEmpty()) {
                    fallbackPoseExercises()
                } else {
                    mergeWithFallback(apiExercises)
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
                val fallbackExercises = fallbackPoseExercises()

                exerciseRepository.clear()
                exerciseRepository.insertAll(fallbackExercises)

                _uiState.update {
                    it.copy(
                        exercises = fallbackExercises,
                        isLoading = false,
                        errorMessage = null
                    )
                }
            }
        }
    }

    private fun isSupportedPoseExercise(name: String, description: String): Boolean {
        val text = "$name $description".lowercase()

        return listOf(
            "bicep",
            "curl",
            "side stretch",
            "forward fold",
            "seated twist"
        ).any { keyword -> keyword in text }
    }

    private fun normaliseExerciseName(name: String, description: String): String {
        val text = "$name $description".lowercase()

        return when {
            "bicep" in text || "curl" in text -> "Bicep Curl"
            "side" in text && "stretch" in text -> "Side Stretch"
            "forward" in text || "fold" in text -> "Forward Fold"
            "seated" in text && "twist" in text -> "Seated Twist"
            else -> name.ifBlank { "Supported Exercise" }
        }
    }

    private fun guessCategory(name: String, description: String): String {
        val text = "$name $description".lowercase()

        return when {
            "bicep" in text || "curl" in text -> "Strength"
            "stretch" in text -> "Flexibility"
            "fold" in text || "twist" in text -> "Mobility"
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
                "Follow the movement slowly and stop if you feel discomfort."
        }
    }

    private fun fallbackPoseExercises(): List<Exercise> {
        return listOf(
            Exercise(
                id = "pose_bicep_curl",
                name = "Bicep Curl",
                category = "Strength",
                instructions = fallbackInstructions("Bicep Curl"),
                imageUrl = null
            ),
            Exercise(
                id = "pose_side_stretch",
                name = "Side Stretch",
                category = "Flexibility",
                instructions = fallbackInstructions("Side Stretch"),
                imageUrl = null
            ),
            Exercise(
                id = "pose_forward_fold",
                name = "Forward Fold",
                category = "Mobility",
                instructions = fallbackInstructions("Forward Fold"),
                imageUrl = null
            ),
            Exercise(
                id = "pose_seated_twist",
                name = "Seated Twist",
                category = "Mobility",
                instructions = fallbackInstructions("Seated Twist"),
                imageUrl = null
            )
        )
    }

    private fun mergeWithFallback(apiExercises: List<Exercise>): List<Exercise> {
        val fallback = fallbackPoseExercises()
        val existingNames = apiExercises.map { it.name }.toSet()
        val missing = fallback.filterNot { it.name in existingNames }

        return apiExercises + missing
    }
}