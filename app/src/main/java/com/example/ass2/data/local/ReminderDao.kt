package com.example.ass2.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ReminderDao {
    @Insert
    suspend fun insert(reminder: Reminder): Long

    @Update
    suspend fun update(reminder: Reminder)

    @Delete
    suspend fun delete(reminder: Reminder)

    // Sorted by time so the list appears in chronological order
    @Query("SELECT * FROM reminders WHERE userId = :userId ORDER BY hour, minute")
    fun getAll(userId: String): Flow<List<Reminder>>

    @Query("SELECT * FROM reminders WHERE id = :id AND userId = :userId")
    fun getById(userId: String, id: Long): Flow<Reminder?>

    // Used by ReminderReceiver - alarm intent only carries the reminder id, not the user context
    @Query("SELECT * FROM reminders WHERE id = :id")
    fun getByIdAny(id: Long): Flow<Reminder?>

    // Used by BootReceiver on device reboot to reschedule only active reminders (all users on this device)
    @Query("SELECT * FROM reminders WHERE enabled = 1")
    fun getEnabled(): Flow<List<Reminder>>

    // Used on logout to cancel + clean reminders belonging to the signing-out user
    @Query("SELECT * FROM reminders WHERE userId = :userId")
    suspend fun listForUser(userId: String): List<Reminder>
}
