package com.example.ass2.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ProfileDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(profile: Profile)

    @Query("SELECT * FROM profile WHERE userId = :uid")
    fun observe(uid: String): Flow<Profile?>

    @Query("DELETE FROM profile WHERE userId = :uid")
    suspend fun delete(uid: String)
}
