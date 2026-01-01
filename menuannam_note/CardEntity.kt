package com.example.menuannam

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Index
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.RawQuery
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteQuery
import kotlinx.serialization.Serializable
import android.content.Context
import androidx.room.Room


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

@Dao
interface FlashCardDao {
    @RawQuery
    fun checkpoint(supportSQLiteQuery: SupportSQLiteQuery): Int

    @Query("SELECT * FROM FlashCards")
    suspend fun getAll(): List<FlashCard>

    //@Query("SELECT * FROM FlashCards LIMIT :size")
    @Query("SELECT * FROM FlashCards ORDER BY RANDOM() LIMIT :size")
    suspend fun getLesson(size: Int): List<FlashCard>

    @Query(
        "SELECT * FROM FlashCards WHERE english_card LIKE :english AND " +
                "vietnamese_card LIKE :vietnamese LIMIT 1"
    )
    suspend fun findByCards(english: String, vietnamese: String): FlashCard?

    @Insert
    suspend fun insertAll(vararg flashCard: FlashCard)

    @Query(
        "UPDATE FlashCards SET english_card = :englishNew " +
                ", vietnamese_card =:vietnameseNew " +
                "WHERE english_card = :englishOld " +
                "AND vietnamese_card = :vietnameseOld"
    )
    suspend fun updateFlashCard(
        englishOld: String, vietnameseOld: String,
        englishNew: String, vietnameseNew: String
    )


    @Query(
        "DELETE FROM FlashCards WHERE english_card = :english " +
                "AND vietnamese_card =:vietnamese"
    )
    suspend fun deleteFlashCard(english: String, vietnamese: String)

}


