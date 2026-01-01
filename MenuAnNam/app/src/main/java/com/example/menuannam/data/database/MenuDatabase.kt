package com.example.menuannam.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.menuannam.data.entity.FlashCard
import kotlin.jvm.Volatile

/**
 * Room Database for FlashCard entity
 * Manages local persistence of flashcard data
 *
 * - entities: [FlashCard] - defines which entity classes to manage
 * - version: 1 - database schema version
 *
 * Provides singleton access via getDatabase(context)
 * Uses lazy initialization with double-checked locking for thread safety
 */
@Database(entities = [FlashCard::class], version = 1)
abstract class FlashCardDatabase : RoomDatabase() {
    /**
     * Abstract function to access FlashCardDao
     * Implemented automatically by Room annotation processor
     */
    abstract fun flashCardDao(): FlashCardDao

    companion object {
        /**
         * @Volatile - Ensures all threads see the latest value immediately
         * Prevents multiple database instances from being created
         */
        @Volatile
        private var INSTANCE: FlashCardDatabase? = null

        /**
         * Gets or creates the single database instance
         * Uses synchronized block to prevent race conditions
         *
         * Flow:
         * 1. Check if INSTANCE already exists (fast path)
         * 2. If not, synchronize to prevent multiple threads creating instances
         * 3. Create database using Room.databaseBuilder
         * 4. Store in INSTANCE and return it
         *
         * @param context Android context (uses application context to prevent memory leaks)
         * @return Single FlashCardDatabase instance
         */
        fun getDatabase(context: Context): FlashCardDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    FlashCardDatabase::class.java,
                    "FlashCardDatabase"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}