package com.example.ass2.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface SessionDao {
    @Insert
    suspend fun insert(session: Session): Long

    @Update
    suspend fun update(session: Session)

    @Delete
    suspend fun delete(session: Session)

    // Returns Flow — Room emits a new list automatically whenever the sessions table changes
    @Query("SELECT * FROM sessions ORDER BY startTime DESC")
    fun getAll(): Flow<List<Session>>

    @Query("SELECT * FROM sessions WHERE exerciseName = :name ORDER BY startTime DESC")
    fun getByExercise(name: String): Flow<List<Session>>

    @Query("SELECT * FROM sessions WHERE id = :id")
    fun getById(id: Long): Flow<Session?>

    // Used by ProgressScreen to filter sessions within a time window (week/month)
    @Query("SELECT * FROM sessions WHERE startTime >= :sinceMillis ORDER BY startTime ASC")
    fun getSince(sinceMillis: Long): Flow<List<Session>>

    @Query("SELECT COUNT(*) FROM sessions WHERE startTime >= :sinceMillis")
    fun countSince(sinceMillis: Long): Flow<Int>

    // IFNULL ensures 0 is returned when no rows match, instead of null
    @Query("SELECT IFNULL(SUM(durationSec), 0) FROM sessions WHERE startTime >= :sinceMillis")
    fun totalDurationSince(sinceMillis: Long): Flow<Int>
}
