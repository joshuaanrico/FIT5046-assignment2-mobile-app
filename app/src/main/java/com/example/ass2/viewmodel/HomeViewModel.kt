package com.example.ass2.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ass2.data.local.Exercise
import com.example.ass2.data.local.Session
import com.example.ass2.data.remote.ApiExerciseInfo
import com.example.ass2.data.remote.RetrofitClient
import com.example.ass2.data.repository.ExerciseRepository
import com.example.ass2.data.repository.SessionRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class HomeUiState(
    val greetingName: String = "there",
    val recommendedExercise: Exercise? = null,
    val recentSession: Session? = null,
    val totalSessions: Int = 0,
    val totalMinutes: Int = 0,
    val adaptiveMessage: String = "Start with a light seated mobility routine today.",
    val isLoadingExercise: Boolean = false,
    val errorMessage: String? = null
)

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val exerciseRepository = ExerciseRepository(application)
    private val sessionRepository = SessionRepository(application)

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val supportedExerciseMap = mapOf(
        1192 to "Bicep Curl",
        1650 to "Side Stretch",
        1652 to "Forward Fold",
        1089 to "Seated Twist"
    )

    init {
        loadGreetingName()
        observeSessions()
        loadRecommendedExercise()
    }

    private fun loadGreetingName() {
        val user = FirebaseAuth.getInstance().currentUser

        val name = user?.displayName
            ?: user?.email?.substringBefore("@")
            ?: "there"

        _uiState.update {
            it.copy(greetingName = name)
        }
    }

    private fun observeSessions() {
        viewModelScope.launch {
            sessionRepository.all.collect { sessions ->
                val recent = sessions.maxByOrNull { it.startTime }
                val totalMinutes = sessions.sumOf { it.durationSec } / 60

                // getting the recommended exercise
                val exercises = exerciseRepository.all.first()
                val recommended = getRecommendedExercise(exercises, recent)

                _uiState.update {
                    it.copy(
                        recentSession = recent,
                        totalSessions = sessions.size,
                        totalMinutes = totalMinutes,
                        adaptiveMessage = buildAdaptiveMessage(sessions),
                        recommendedExercise = recommended
                    )
                }
            }
        }
    }

    private fun loadRecommendedExercise() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _uiState.update {
                    it.copy(
                        isLoadingExercise = true,
                        errorMessage = null
                    )
                }

                val cachedExercises = exerciseRepository.all.first()

                if (cachedExercises.isNotEmpty()) {
                    _uiState.update {
                        it.copy(isLoadingExercise= false)
                    }
                    return@launch
                }

                val response = RetrofitClient.apiService.getExerciseInfoList()

                val apiExercises = response.results
                    .filter { item -> supportedExerciseMap.containsKey(item.id) }
                    .map { item ->
                        item.toRoomExercise(
                            appDisplayName = supportedExerciseMap[item.id] ?: "Exercise"
                        )
                    }
                    .sortedBy { exerciseOrder(it.name) }

                val finalExercises = if (apiExercises.isNotEmpty()) {
                    apiExercises
                } else {
                    fallbackPoseExercises()
                }

                exerciseRepository.insertAll(finalExercises)

                _uiState.update {
                    it.copy(
                        isLoadingExercise = false,
                        errorMessage = null
                    )
                }
            } catch (e: Exception) {
                val fallback = fallbackPoseExercises()

                _uiState.update {
                    it.copy(
                        isLoadingExercise = false,
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

    /**
     * Based on the users most recent session feedback an exercise is picked for the user to do.
     *
     * @param exercises list of exercises from the room db.
     * @param recent the most recently saved session.
     * @return the recommended exercise.
     */
    private fun getRecommendedExercise(exercises: List<Exercise>, recent: Session?) : Exercise?
    {
        if (exercises.isEmpty()) return null

        // a local helper fun to classify each category string to an intensity score.
        fun intensityScore(category: String): Int = when (category.lowercase())
        {
            "stretch" -> 1
            "mobility" -> 2
            "core" -> 3
            "strength" -> 4
            else -> 2  // for a category not defined
        }

        // according to the users last session feedback, deciding on the new intensity to be recommended
        val targetIntensity = when {
            // when no session history available
            recent == null -> 2
            recent.fatigueLevel == "Fatigued" || recent.discomfort == "Severe" -> 1
            recent.fatigueLevel == "Energized" && recent.discomfort == "None" -> 1
            else -> 2
        }

        // gets the exercise whose category intensity is closest to the target intensity.
        // minByOrNull - ensures the return of the first match (if multiple options exist)
        return exercises.minByOrNull {
            kotlin.math.abs(intensityScore(it.category) - targetIntensity)
        }
    }

    private fun buildAdaptiveMessage(sessions: List<Session>): String {
        if (sessions.isEmpty()) {
            return "Start small today. A 10-minute seated mobility session is enough to build momentum."
        }

        val latestSession = sessions.maxByOrNull { it.startTime }

        return when {
            latestSession?.avgHr != null && latestSession.avgHr!! > 110 ->
                "Your last heart rate was high. Choose a slower recovery session today."

            sessions.size >= 5 ->
                "You are building consistency. Try a slightly longer mobility flow today."

            else ->
                "Keep the routine light and consistent. One short session today still counts."
        }
    }
}