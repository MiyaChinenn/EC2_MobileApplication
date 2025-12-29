package com.example.menuannam.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.menuannam.data.entity.FlashCard
import kotlin.jvm.Volatile

@Database(entities = [FlashCard::class], version = 1)
abstract class MenuDatabase : RoomDatabase() {
    abstract fun flashCardDao(): FlashCardDao

    companion object {
        @Volatile
        private var INSTANCE: MenuDatabase? = null

        fun getDatabase(context: Context): MenuDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MenuDatabase::class.java,
                    "FlashCardDatabase"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}