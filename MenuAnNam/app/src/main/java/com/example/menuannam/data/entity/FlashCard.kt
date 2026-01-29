package com.example.menuannam.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

// FlashCard entity with unique index on (english_card, vietnamese_card) to prevent duplicates
@Entity(tableName = "FlashCards", indices = [Index(
    value = ["english_card", "vietnamese_card"],
    unique = true // Database-level constraint enforced by Room
)])
@Serializable
data class FlashCard(
    @PrimaryKey(autoGenerate = true) val uid: Int, // Auto-increment from 1
    @ColumnInfo(name = "english_card") val englishCard: String?,
    @ColumnInfo(name = "vietnamese_card") val vietnameseCard: String?
)