package com.example.menuannam.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RawQuery
import androidx.sqlite.db.SupportSQLiteQuery
import com.example.menuannam.data.entity.FlashCard

@Dao
interface FlashCardDao {
    @RawQuery
    fun checkpoint(supportSQLiteQuery: SupportSQLiteQuery): Int

    @Query("SELECT * FROM FlashCards")
    suspend fun getAll(): List<FlashCard>

    @Query("SELECT * FROM FlashCards ORDER BY RANDOM() LIMIT :size")
    suspend fun getLesson(size: Int): List<FlashCard>

    @Query(
        "SELECT * FROM FlashCards WHERE english_card LIKE :english AND " +
                "vietnamese_card LIKE :vietnamese LIMIT 1"
    )
    suspend fun findByCards(english: String, vietnamese: String): FlashCard?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(flashCard: FlashCard): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(vararg flashCard: FlashCard)

    @Query(
        "UPDATE FlashCards SET english_card = :englishNew " +
                ", vietnamese_card =:vietnameseNew " +
                "WHERE english_card = :englishOld " +
                "AND vietnamese_card = :vietnameseOld"
    )
    suspend fun updateFlashCard(
        englishOld: String,
        vietnameseOld: String,
        englishNew: String,
        vietnameseNew: String
    )

    @Query(
        "DELETE FROM FlashCards WHERE english_card = :english " +
                "AND vietnamese_card =:vietnamese"
    )
    suspend fun deleteFlashCard(english: String, vietnamese: String)

    // Existing operations kept for compatibility
    @Query("SELECT * FROM FlashCards WHERE uid = :id")
    suspend fun getById(id: Int): FlashCard?

    @Query("SELECT COUNT(*) FROM FlashCards")
    suspend fun getCount(): Int

    @Query("UPDATE FlashCards SET english_card = :english, vietnamese_card = :vietnamese WHERE uid = :id")
    suspend fun update(id: Int, english: String, vietnamese: String)

    @Delete
    suspend fun delete(flashCard: FlashCard)
}