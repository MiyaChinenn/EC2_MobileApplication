package com.example.menuannam.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RawQuery
import androidx.sqlite.db.SupportSQLiteQuery
import com.example.menuannam.data.entity.FlashCard

/**
 * Data Access Object (DAO) for FlashCard entity
 * Provides methods to interact with the FlashCards table
 * All suspend functions run on background thread (Room default)
 *
 * OnConflictStrategy.IGNORE - if duplicate exists, silently ignore (don't update)
 */
@Dao
interface FlashCardDao {
    /**
     * Executes raw SQL query for checkpoint operations
     * Used for database maintenance if needed
     */
    @RawQuery
    fun checkpoint(supportSQLiteQuery: SupportSQLiteQuery): Int

    /**
     * Retrieves ALL flashcards from the database
     * @return List of all FlashCard objects
     * Flow: Database -> Cursor -> FlashCard list
     */
    @Query("SELECT * FROM FlashCards")
    suspend fun getAll(): List<FlashCard>

    /**
     * Gets random flashcards (shuffled) for study sessions
     * @param size Number of cards to retrieve (e.g., 5 cards per lesson)
     * @return Random list of FlashCards
     * Flow: Database with ORDER BY RANDOM() -> shuffle cards -> return limited set
     */
    @Query("SELECT * FROM FlashCards ORDER BY RANDOM() LIMIT :size")
    suspend fun getLesson(size: Int): List<FlashCard>

    /**
     * Finds a flashcard by matching both English and Vietnamese text
     * Used for duplicate detection
     * @param english English text to search
     * @param vietnamese Vietnamese text to search
     * @return Single matching FlashCard or null if not found
     */
    @Query(
        "SELECT * FROM FlashCards WHERE english_card LIKE :english AND " +
                "vietnamese_card LIKE :vietnamese LIMIT 1"
    )
    suspend fun findByCards(english: String, vietnamese: String): FlashCard?

    /**
     * Inserts a single flashcard
     * OnConflictStrategy.IGNORE - if card exists, do nothing (returns -1)
     * @param flashCard Card to insert
     * @return Row ID of inserted card, or -1 if ignored
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(flashCard: FlashCard): Long

    /**
     * Inserts multiple flashcards at once
     * OnConflictStrategy.IGNORE - duplicates are silently ignored
     * @param flashCard Variable number of FlashCard objects to insert
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(vararg flashCard: FlashCard)

    /**
     * Updates a flashcard by matching old English/Vietnamese and replacing with new values
     * @param englishOld Previous English text (search key)
     * @param vietnameseOld Previous Vietnamese text (search key)
     * @param englishNew New English text (new value)
     * @param vietnameseNew New Vietnamese text (new value)
     */
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

    /**
     * Deletes a flashcard by matching English and Vietnamese text
     * @param english English text to match for deletion
     * @param vietnamese Vietnamese text to match for deletion
     */
    @Query(
        "DELETE FROM FlashCards WHERE english_card = :english " +
                "AND vietnamese_card =:vietnamese"
    )
    suspend fun deleteFlashCard(english: String, vietnamese: String)

    // ===== Additional CRUD operations =====

    /**
     * Retrieves a single flashcard by its unique ID
     * @param id The uid (primary key) of the card
     * @return FlashCard if found, null otherwise
     */
    @Query("SELECT * FROM FlashCards WHERE uid = :id")
    suspend fun getById(id: Int): FlashCard?

    /**
     * Gets total number of flashcards in database
     * @return Count of all cards
     */
    @Query("SELECT COUNT(*) FROM FlashCards")
    suspend fun getCount(): Int

    /**
     * Updates a card identified by ID with new English/Vietnamese text
     * @param id Card ID (uid) to update
     * @param english New English text
     * @param vietnamese New Vietnamese text
     */
    @Query("UPDATE FlashCards SET english_card = :english, vietnamese_card = :vietnamese WHERE uid = :id")
    suspend fun update(id: Int, english: String, vietnamese: String)

    /**
     * Deletes a complete FlashCard object
     * @param flashCard The FlashCard entity to delete
     */
    @Delete
    suspend fun delete(flashCard: FlashCard)
}