package com.example.ass2.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ass2.data.local.Exercise
import com.example.ass2.data.local.Session
import com.example.ass2.data.remote.RetrofitClient
import com.example.ass2.data.repository.ExerciseRepository
import com.example.ass2.data.repository.SessionRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.google.firebase.auth.FirebaseAuth

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

    init {
        observeSessions()
        loadRecommendedExercise()
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

                _uiState.update {
                    it.copy(
                        recentSession = recent,
                        totalSessions = sessions.size,
                        totalMinutes = totalMinutes,
                        adaptiveMessage = buildAdaptiveMessage(sessions)
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
                        it.copy(
                            recommendedExercise = cachedExercises.first(),
                            isLoadingExercise = false
                        )
                    }
                    return@launch
                }

                val response = RetrofitClient.apiService.getExercises()

                val apiExercises = response.results.map { item ->
                    Exercise(
                        id = item.id.toString(),
                        name = item.name.ifBlank { "Seated Mobility" },
                        category = "Mobility",
                        instructions = item.description
                            .replace(Regex("<.*?>"), "")
                            .ifBlank {
                                "A gentle movement routine to support comfort and circulation."
                            },
                        imageUrl = null
                    )
                }

                exerciseRepository.insertAll(apiExercises)

                _uiState.update {
                    it.copy(
                        recommendedExercise = apiExercises.firstOrNull(),
                        isLoadingExercise = false
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoadingExercise = false,
                        errorMessage = "Could not load recommended exercise."
                    )
                }
            }
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