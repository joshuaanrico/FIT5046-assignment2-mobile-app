package com.example.ass2.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "profile")
data class Profile(
    @PrimaryKey val userId: String,         // Firebase Auth uid - string PK avoids sync issues on reinstall
    val displayName: String,
    val dob: Long? = null,                  // epoch millis; null until ProfileSetup completed
    val heightCm: Float? = null,
    val weightKg: Float? = null,
    val conditions: String? = null,         // free-text related conditions (optional)
    val painBaseline: Int? = null,          // 0..10 baseline from ProfileSetup
    val mobilitySupport: String? = null,    // e.g. "Wheelchair", "Cane / Walker"
    val activityLevel: String? = null,      // e.g. "Sedentary", "Lightly Active"
    val age: Int? = null,
    val restingHeartRate: Int? = null       // bpm
)
