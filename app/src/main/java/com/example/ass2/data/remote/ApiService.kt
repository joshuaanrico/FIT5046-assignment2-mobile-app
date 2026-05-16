package com.example.ass2.data.remote

import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("exerciseinfo/")
    suspend fun getExerciseInfoList(
        @Query("language") language: Int = 2,
        @Query("limit") limit: Int = 1000
    ): ExerciseInfoListResponse
}

data class ExerciseInfoListResponse(
    val results: List<ApiExerciseInfo> = emptyList()
)

data class ApiExerciseInfo(
    val id: Int,
    val category: ApiExerciseCategory?,
    val muscles: List<ApiMuscle> = emptyList(),
    val muscles_secondary: List<ApiMuscle> = emptyList(),
    val equipment: List<ApiEquipment> = emptyList(),
    val images: List<ApiExerciseImage> = emptyList(),
    val translations: List<ApiExerciseTranslation> = emptyList()
)

data class ApiExerciseCategory(
    val id: Int?,
    val name: String?
)

data class ApiMuscle(
    val id: Int?,
    val name: String?,
    val name_en: String?
)

data class ApiEquipment(
    val id: Int?,
    val name: String?
)

data class ApiExerciseImage(
    val image: String?
)

data class ApiExerciseTranslation(
    val language: Int?,
    val name: String?,
    val description: String?,
    val description_source: String?
)