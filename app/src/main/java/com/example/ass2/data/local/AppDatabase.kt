package com.example.ass2.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [Session::class, Reminder::class, Profile::class, Exercise::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun sessionDao(): SessionDao
    abstract fun reminderDao(): ReminderDao
    abstract fun profileDao(): ProfileDao
    abstract fun exerciseDao(): ExerciseDao

    companion object {
        // @Volatile ensures INSTANCE is always read from main memory, not a thread-local cache
        @Volatile
        private var INSTANCE: AppDatabase? = null

        // Singleton pattern — only one DB connection shared across the whole app
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "sitfit-db"
                )
                    .fallbackToDestructiveMigration(true) // dev only — wipes DB on schema change instead of crashing
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
