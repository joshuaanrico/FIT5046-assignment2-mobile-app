package com.example.ass2.pose

data class ExerciseUpdate(
    val repCount: Int = 0,
    val state: String = "",
    val feedback: String = "",
    val poseQuality: Float = 0f,
    val leftAngle: Float = 0f,
    val rightAngle: Float = 0f,
)
