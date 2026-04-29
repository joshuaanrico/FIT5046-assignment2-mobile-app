package com.example.ass2.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ExerciseDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<Exercise>)

    @Query("SELECT * FROM exercises ORDER BY name")
    fun getAll(): Flow<List<Exercise>>

    @Query("SELECT * FROM exercises WHERE category = :cat ORDER BY name")
    fun getByCategory(cat: String): Flow<List<Exercise>>

    @Query("SELECT * FROM exercises WHERE id = :id")
    fun getById(id: String): Flow<Exercise?>

    @Query("DELETE FROM exercises")
    suspend fun clear()
}
