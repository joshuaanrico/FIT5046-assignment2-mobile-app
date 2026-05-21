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

    // Returns Flow - Room emits a new list automatically whenever the sessions table changes
    @Query("SELECT * FROM sessions WHERE userId = :userId ORDER BY startTime DESC")
    fun getAll(userId: String): Flow<List<Session>>

    @Query("SELECT * FROM sessions WHERE userId = :userId AND exerciseName = :name ORDER BY startTime DESC")
    fun getByExercise(userId: String, name: String): Flow<List<Session>>

    // id is globally unique (autogenerate) but still filter by userId so users can't deep-link into another user's row
    @Query("SELECT * FROM sessions WHERE id = :id AND userId = :userId")
    fun getById(userId: String, id: Long): Flow<Session?>

    // Used by ProgressScreen to filter sessions within a time window (week/month)
    @Query("SELECT * FROM sessions WHERE userId = :userId AND startTime >= :sinceMillis ORDER BY startTime ASC")
    fun getSince(userId: String, sinceMillis: Long): Flow<List<Session>>

    @Query("SELECT COUNT(*) FROM sessions WHERE userId = :userId AND startTime >= :sinceMillis")
    fun countSince(userId: String, sinceMillis: Long): Flow<Int>

    // IFNULL ensures 0 is returned when no rows match, instead of null
    @Query("SELECT IFNULL(SUM(durationSec), 0) FROM sessions WHERE userId = :userId AND startTime >= :sinceMillis")
    fun totalDurationSince(userId: String, sinceMillis: Long): Flow<Int>

    // Used on logout to clear scheduled-alarm side effects if ever needed (not currently called for sessions)
    @Query("DELETE FROM sessions WHERE userId = :userId")
    suspend fun deleteAllForUser(userId: String)
}
