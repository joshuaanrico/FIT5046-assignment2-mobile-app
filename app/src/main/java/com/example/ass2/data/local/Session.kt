package com.example.ass2.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sessions")
data class Session(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val userId: String = "",        // Firebase Auth uid - isolates rows per user (stamped by SessionViewModel.insert)
    val startTime: Long,            // epoch millis - sort/filter in SessionsScreen, window in ProgressScreen
    val durationSec: Int,           // shown in SessionDetail, aggregated in ProgressScreen
    val exerciseName: String,       // SessionsScreen list label and filter chip
    val exerciseId: String? = null, // links to Exercise cache (nullable: user may log free session)
    val avgHr: Int? = null,         // nullable - populated by heart-rate sensor pipeline when available
    val poseQuality: Float? = null, // nullable - populated by MediaPipe pose pipeline when available
    val notes: String? = null,       // editable in SessionDetailScreen
    val fatigueLevel: String? = null,
    val discomfort: String? = null
)
