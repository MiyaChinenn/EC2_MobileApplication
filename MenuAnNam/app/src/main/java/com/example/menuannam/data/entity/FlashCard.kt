package com.example.menuannam.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

/**
 * FlashCard data entity
 * Represents a single flashcard in the database
 * Each flashcard has an English word and Vietnamese translation
 *
 * @Entity annotation marks this as a Room database table named "FlashCards"
 * @Serializable allows Gson to serialize/deserialize from JSON
 *
 * Unique Index on (english_card, vietnamese_card):
 * - Prevents duplicate cards with same English and Vietnamese pair
 * - Database enforces unique constraint at the table level
 *
 * Fields:
 * - uid: Auto-generated primary key (starts from 1)
 * - englishCard: English word/phrase (can be null)
 * - vietnameseCard: Vietnamese translation (can be null)
 */
@Entity(tableName = "FlashCards", indices = [Index(
    value = ["english_card", "vietnamese_card"],
    unique = true
)])
@Serializable
data class FlashCard(
    @PrimaryKey(autoGenerate = true) val uid: Int,
    @ColumnInfo(name = "english_card") val englishCard: String?,
    @ColumnInfo(name = "vietnamese_card") val vietnameseCard: String?
)