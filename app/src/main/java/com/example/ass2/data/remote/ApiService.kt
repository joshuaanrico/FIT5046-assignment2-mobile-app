package com.example.ass2.data.remote

import retrofit2.http.GET

interface ApiService {
    @GET("exercise/?language=2&limit=50")
    suspend fun getExercises(): ExerciseResponse
}

data class ExerciseResponse(
    val results: List<ApiExercise>
)

data class ApiExercise(
    val id: Int,
    val name: String?,
    val description: String?
)