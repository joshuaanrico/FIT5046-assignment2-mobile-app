package com.example.ass2.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "exercises")
data class Exercise(
    @PrimaryKey val id: String,       // remote API id from wger
    val name: String,                 // ExercisesScreen list
    val category: String,             // filter chip on ExercisesScreen
    val instructions: String,         // ExerciseDetailScreen body
    val imageUrl: String? = null      // ExercisesScreen cards
)
