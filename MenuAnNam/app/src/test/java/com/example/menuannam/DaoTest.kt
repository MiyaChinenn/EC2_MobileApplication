//package com.example.menuannam
//
//import android.content.Context
//import android.database.sqlite.SQLiteConstraintException
//import androidx.room.Room
//import androidx.test.core.app.ApplicationProvider
//import kotlinx.coroutines.runBlocking
//import org.junit.After
//import org.junit.Assert.assertEquals
//import org.junit.Before
//import org.junit.Rule
//import org.junit.Test
//import org.junit.runner.RunWith
//import org.robolectric.RobolectricTestRunner
//import com.example.menuannam.data.entity.FlashCard
//import com.example.menuannam.data.database.FlashCardDao
//import com.example.menuannam.data.database.MenuDatabase
//
//
//@RunWith(RobolectricTestRunner::class)
//class DaoTest {
//    @get:Rule
//    private lateinit var db: MenuDatabase
//    private lateinit var flashCardDao: FlashCardDao
//
//
//    @Before
//    fun setup() {
//        val context = ApplicationProvider.getApplicationContext<Context>()
//        db = Room.inMemoryDatabaseBuilder(
//            context, MenuDatabase::class.java).build()
//        flashCardDao = db.flashCardDao()
//    }
//
//    @After
//    fun close(){
//        db.close()
//    }
//
//
//    @Test
//    fun insertFlashCardSuccessful() {
//        val flashCard =
//            FlashCard(
//                uid = 0,
//                englishCard = "test_english",
//                vietnameseCard = "test_vietnamese"
//            )
//
//        runBlocking {
//            flashCardDao.insertAll(flashCard)
//        }
//
//        val item:FlashCard?
//        runBlocking {
//            item = flashCardDao.findByCards("test_english", "test_vietnamese")
//        }
//        assertEquals(flashCard.englishCard, item!!.englishCard, )
//        assertEquals(flashCard.vietnameseCard, item!!.vietnameseCard)
//    }
//
//    @Test
//    fun insertFlashCardUnSuccessful() {
//        val context = ApplicationProvider.getApplicationContext<Context>()
//        db = Room.inMemoryDatabaseBuilder(
//            context, MenuDatabase::class.java).build()
//        flashCardDao = db.flashCardDao()
//
//        val flashCard =
//            FlashCard(
//                uid = 0,
//                englishCard = "test_english",
//                vietnameseCard = "test_vietnamese"
//            )
//
//        runBlocking {
//            flashCardDao.insertAll(flashCard)
//        }
//        var countBefore = 0
//        runBlocking {
//            countBefore = flashCardDao.getCount()
//        }
//        runBlocking {
//            // With OnConflictStrategy.IGNORE, duplicate insert does not throw, just ignores
//            flashCardDao.insertAll(flashCard)
//        }
//        var countAfter = 0
//        runBlocking {
//            countAfter = flashCardDao.getCount()
//        }
//        // Both counts should be equal (1), meaning duplicate was ignored
//        assertEquals(countBefore, countAfter)
//    }
//
//    /* Delete */
//    @Test
//    fun deleteExistingFlashCard() {
//        val flashCard =
//            FlashCard(
//                uid = 0,
//                englishCard = "test_english",
//                vietnameseCard = "test_vietnamese"
//            )
//
//        var flashCardsBefore: List<FlashCard>
//        runBlocking {
//            flashCardsBefore = flashCardDao.getAll()
//        }
//        runBlocking{
//            flashCardDao.insertAll(flashCard)
//            flashCardDao.deleteFlashCard("test_english",
//                vietnamese = "test_vietnamese")
//        }
//        var flashCardsAfter: List<FlashCard>
//        runBlocking {
//            flashCardsAfter = flashCardDao.getAll()
//        }
//        assertEquals(flashCardsBefore, flashCardsAfter)
//    }
//
//
//    @Test
//    fun deleteNonExistingFlashCard() {
//        val context = ApplicationProvider.getApplicationContext<Context>()
//        db = Room.inMemoryDatabaseBuilder(
//            context, MenuDatabase::class.java
//        ).build()
//        flashCardDao = db.flashCardDao()
//
//        val flashCard =
//            FlashCard(
//                uid = 0,
//                englishCard = "test_english",
//                vietnameseCard = "test_vietnamese"
//            )
//
//        var flashCardsBefore: List<FlashCard>
//        runBlocking {
//            flashCardDao.insertAll(flashCard)
//            flashCardsBefore = flashCardDao.getAll()
//        }
//        runBlocking {
//            flashCardDao.deleteFlashCard(
//                "test_english_1",
//                vietnamese = "test_vietnamese_1"
//            )
//        }
//        var flashCardsAfter: List<FlashCard>
//        runBlocking {
//            flashCardsAfter = flashCardDao.getAll()
//        }
//        assertEquals(flashCardsBefore, flashCardsAfter)
//    }
//    /* Similar for the other 2 cases */
//}