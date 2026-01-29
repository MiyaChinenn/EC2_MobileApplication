package com.example.menuannam.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RawQuery
import androidx.sqlite.db.SupportSQLiteQuery
import com.example.menuannam.data.entity.FlashCard

// DAO for FlashCard CRUD operations; all suspend functions run on background thread
@Dao
interface FlashCardDao {
    // Raw SQL for database checkpoint operations
    @RawQuery
    fun checkpoint(supportSQLiteQuery: SupportSQLiteQuery): Int

    @Query("SELECT * FROM FlashCards")
    suspend fun getAll(): List<FlashCard>

    // Random shuffle for study sessions
    @Query("SELECT * FROM FlashCards ORDER BY RANDOM() LIMIT :size")
    suspend fun getLesson(size: Int): List<FlashCard>

    // Duplicate detection by matching both fields
    @Query(
        "SELECT * FROM FlashCards WHERE english_card LIKE :english AND " +
                "vietnamese_card LIKE :vietnamese LIMIT 1"
    )
    suspend fun findByCards(english: String, vietnamese: String): FlashCard?

    // IGNORE strategy: silently skip if duplicate exists (returns -1)
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(flashCard: FlashCard): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(vararg flashCard: FlashCard)

    // Update by old English/Vietnamese pair (search key) to new values
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

    @Query("SELECT * FROM FlashCards WHERE uid = :id")
    suspend fun getById(id: Int): FlashCard?

    @Query("SELECT COUNT(*) FROM FlashCards")
    suspend fun getCount(): Int

    @Query("UPDATE FlashCards SET english_card = :english, vietnamese_card = :vietnamese WHERE uid = :id")
    suspend fun update(id: Int, english: String, vietnamese: String)

    @Delete
    suspend fun delete(flashCard: FlashCard)

    // CASE WHEN for exact vs partial match: exactEn=1 → LIKE :en, exactEn=0 → LIKE '%' || :en || '%'
    @Query(
        "SELECT * FROM FlashCards WHERE " +
                "(CASE WHEN :exactEn THEN english_card LIKE :en " +
                "WHEN NOT :exactEn THEN english_card LIKE '%' || :en || '%' END) " +
                "AND " +
                "(CASE WHEN :exactVn THEN vietnamese_card LIKE :vn " +
                "WHEN NOT :exactVn THEN vietnamese_card LIKE '%' || :vn || '%' END)"
    )
    suspend fun getFilteredFlashCards(en: String, exactEn: Int, vn: String, exactVn: Int): List<FlashCard>
}