package com.example.menuannam.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.menuannam.data.entity.FlashCard
import kotlin.jvm.Volatile

// Room database with singleton pattern for thread-safe FlashCard persistence
@Database(entities = [FlashCard::class], version = 1)
abstract class FlashCardDatabase : RoomDatabase() {
    abstract fun flashCardDao(): FlashCardDao

    companion object {
        @Volatile // All threads see latest value immediately
        private var INSTANCE: FlashCardDatabase? = null

        // Double-checked locking: fast path check, then synchronized creation to prevent race conditions
        fun getDatabase(context: Context): FlashCardDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext, // Use app context to prevent memory leaks
                    FlashCardDatabase::class.java,
                    "FlashCardDatabase"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}