package com.example.menuannam

import android.database.sqlite.SQLiteConstraintException
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.StateRestorationTester
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import com.example.menuannam.data.entity.FlashCard
import com.example.menuannam.data.database.FlashCardDao
import com.example.menuannam.data.database.MenuDatabase
import com.example.menuannam.presentation.navigation.AppNavigation

import com.example.menuannam.data.network.NetworkService
import com.example.menuannam.data.network.TokenResponse
import com.example.menuannam.data.network.AudioRequest

class DummyFlashCardDao : FlashCardDao {
    override fun checkpoint(supportSQLiteQuery: androidx.sqlite.db.SupportSQLiteQuery): Int = 0

    override suspend fun getAll(): List<FlashCard> {
        return emptyList<FlashCard>()
    }

    override suspend fun getLesson(size: Int): List<FlashCard> {
        return emptyList<FlashCard>()
    }

    override suspend fun getById(id: Int): FlashCard? {
        return FlashCard(0, "", "")
    }

    override suspend fun findByCards(english: String, vietnamese: String): FlashCard? {
        return FlashCard(0, "", "")
    }

    override suspend fun getCount(): Int {
        return 0
    }

    override suspend fun insert(flashCard: FlashCard): Long {
        return 1L
    }

    override suspend fun insertAll(vararg flashCard: FlashCard) {
    }

    override suspend fun update(id: Int, english: String, vietnamese: String) {
    }

    override suspend fun delete(flashCard: FlashCard) {
    }

    override suspend fun deleteFlashCard(english: String, vietnamese: String) {
    }

    override suspend fun updateFlashCard(englishOld: String, vietnameseOld: String, englishNew: String, vietnameseNew: String) {
    }
}

class DummyFlashCardDaoUnsuccessfulInsert : FlashCardDao {
    override fun checkpoint(supportSQLiteQuery: androidx.sqlite.db.SupportSQLiteQuery): Int = 0

    override suspend fun getAll(): List<FlashCard> {
        return emptyList<FlashCard>()
    }

    override suspend fun getLesson(size: Int): List<FlashCard> {
        return emptyList<FlashCard>()
    }

    override suspend fun getById(id: Int): FlashCard? {
        return FlashCard(0, "", "")
    }

    override suspend fun findByCards(english: String, vietnamese: String): FlashCard? {
        return FlashCard(0, "", "")
    }

    override suspend fun getCount(): Int {
        return 0
    }

    override suspend fun insert(flashCard: FlashCard): Long {
        throw SQLiteConstraintException()
    }

    override suspend fun insertAll(vararg flashCard: FlashCard) {
        throw SQLiteConstraintException()
    }

    override suspend fun update(id: Int, english: String, vietnamese: String) {
    }

    override suspend fun delete(flashCard: FlashCard) {
    }

    override suspend fun deleteFlashCard(english: String, vietnamese: String) {
    }

    override suspend fun updateFlashCard(englishOld: String, vietnameseOld: String, englishNew: String, vietnameseNew: String) {
    }
}

class DummyNetworkService : NetworkService {
    override suspend fun generateToken(url: String, credential: com.example.menuannam.data.network.UserCredential): TokenResponse {
        return TokenResponse(code = 200, message = "Success")
    }

    override suspend fun generateAudio(url: String, body: AudioRequest): TokenResponse {
        return TokenResponse(code = 200, message = "dGVzdA==") // Base64 "test"
    }
}

@RunWith(RobolectricTestRunner::class)
class ScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun homeStartDestination() {
        val navController = TestNavHostController(ApplicationProvider.getApplicationContext())
        navController.navigatorProvider.addNavigator(ComposeNavigator())
        val dummyFlashCardDao = DummyFlashCardDao()
        val dummyNetworkService = DummyNetworkService()
        
        composeTestRule.setContent {
            AppNavigation(
                navController = navController,
                flashCardDao = dummyFlashCardDao,
                networkService = dummyNetworkService
            )
        }
        assertEquals("Main", navController.currentDestination?.route)
    }

    @Test
    fun clickOnStudy() {
        val navController = TestNavHostController(ApplicationProvider.getApplicationContext())
        navController.navigatorProvider.addNavigator(ComposeNavigator())
        val dummyFlashCardDao = DummyFlashCardDao()
        val dummyNetworkService = DummyNetworkService()
        
        composeTestRule.setContent {
            AppNavigation(
                navController = navController,
                flashCardDao = dummyFlashCardDao,
                networkService = dummyNetworkService
            )
        }
        
        composeTestRule.onNodeWithContentDescription("navigateToStudyCards")
            .assertExists()
            .performClick()
        assertEquals("Study", navController.currentDestination?.route)
    }

    @Test
    fun clickOnAddCard() {
        val navController = TestNavHostController(ApplicationProvider.getApplicationContext())
        navController.navigatorProvider.addNavigator(ComposeNavigator())
        val dummyFlashCardDao = DummyFlashCardDao()
        val dummyNetworkService = DummyNetworkService()

        composeTestRule.setContent {
            AppNavigation(
                navController = navController,
                flashCardDao = dummyFlashCardDao,
                networkService = dummyNetworkService
            )
        }
        
        composeTestRule.onNodeWithContentDescription("navigateToAddCard")
            .assertExists()
            .performClick()
        assertEquals("Add", navController.currentDestination?.route)
    }

    @Test
    fun clickOnSearchCards() {
        val navController = TestNavHostController(ApplicationProvider.getApplicationContext())
        navController.navigatorProvider.addNavigator(ComposeNavigator())
        val dummyFlashCardDao = DummyFlashCardDao()
        val dummyNetworkService = DummyNetworkService()

        composeTestRule.setContent {
            AppNavigation(
                navController = navController,
                flashCardDao = dummyFlashCardDao,
                networkService = dummyNetworkService
            )
        }
        
        composeTestRule.onNodeWithContentDescription("navigateToSearchCards")
            .assertExists()
            .performClick()
        assertEquals("Search", navController.currentDestination?.route)
    }

    @Test
    fun homeScreenRetained_afterConfigChange() {
        val stateRestorationTester = StateRestorationTester(composeTestRule)
        val navController = TestNavHostController(ApplicationProvider.getApplicationContext())
        navController.navigatorProvider.addNavigator(ComposeNavigator())
        val dummyFlashCardDao = DummyFlashCardDao()
        val dummyNetworkService = DummyNetworkService()

        stateRestorationTester.setContent {
            AppNavigation(
                navController = navController,
                flashCardDao = dummyFlashCardDao,
                networkService = dummyNetworkService
            )
        }
        
        stateRestorationTester.emulateSavedInstanceStateRestore()
        assertEquals("Main", navController.currentDestination?.route)
    }

    @Test
    fun clickOnAddCardAndBack() {
        val navController = TestNavHostController(ApplicationProvider.getApplicationContext())
        navController.navigatorProvider.addNavigator(ComposeNavigator())
        val dummyFlashCardDao = DummyFlashCardDao()
        val dummyNetworkService = DummyNetworkService()
        
        composeTestRule.setContent {
            AppNavigation(
                navController = navController,
                flashCardDao = dummyFlashCardDao,
                networkService = dummyNetworkService
            )
        }
        
        composeTestRule.onNodeWithContentDescription("navigateToAddCard")
            .performClick()
        
        composeTestRule.onNodeWithContentDescription("navigateBack")
            .assertExists()
            .performClick()
        assertEquals("Main", navController.currentDestination?.route)
    }

    @Test
    fun typeOnEnTextInput() {
        val navController = TestNavHostController(ApplicationProvider.getApplicationContext())
        navController.navigatorProvider.addNavigator(ComposeNavigator())
        val dummyFlashCardDao = DummyFlashCardDao()
        val dummyNetworkService = DummyNetworkService()

        composeTestRule.setContent {
            AppNavigation(
                navController = navController,
                flashCardDao = dummyFlashCardDao,
                networkService = dummyNetworkService
            )
        }
        
        composeTestRule.onNodeWithContentDescription("navigateToAddCard")
            .performClick()

        val textInput = "house"
        composeTestRule.onNodeWithContentDescription("enTextField")
            .assertExists()
            .performTextInput(textInput)
        composeTestRule.onNodeWithContentDescription("enTextField")
            .assertTextEquals("en", textInput)
    }

    @Test
    fun keepEnglishStringAfterRotation() {
        val navController = TestNavHostController(ApplicationProvider.getApplicationContext())
        navController.navigatorProvider.addNavigator(ComposeNavigator())
        val dummyFlashCardDao = DummyFlashCardDao()
        val dummyNetworkService = DummyNetworkService()

        val stateRestorationTester = StateRestorationTester(composeTestRule)
        stateRestorationTester.setContent {
            AppNavigation(
                navController = navController,
                flashCardDao = dummyFlashCardDao,
                networkService = dummyNetworkService
            )
        }
        
        composeTestRule.onNodeWithContentDescription("navigateToAddCard")
            .performClick()

        val textInput = "house"
        composeTestRule.onNodeWithContentDescription("enTextField").assertExists()
            .performTextInput(textInput)

        stateRestorationTester.emulateSavedInstanceStateRestore()
        composeTestRule.onNodeWithContentDescription("enTextField")
            .assertTextEquals("en", textInput)
    }

    @Test
    fun clickOnAddCardSuccessful() {
        val navController = TestNavHostController(ApplicationProvider.getApplicationContext())
        navController.navigatorProvider.addNavigator(ComposeNavigator())
        val dummyFlashCardDao = DummyFlashCardDao()
        val dummyNetworkService = DummyNetworkService()
        
        composeTestRule.setContent {
            AppNavigation(
                navController = navController,
                flashCardDao = dummyFlashCardDao,
                networkService = dummyNetworkService
            )
        }
        
        composeTestRule.onNodeWithContentDescription("navigateToAddCard")
            .performClick()

        composeTestRule.onNodeWithContentDescription("enTextField")
            .performTextInput("test")
        composeTestRule.onNodeWithContentDescription("vnTextField")
            .performTextInput("thử nghiệm")
        
        composeTestRule.onNodeWithContentDescription("Add")
            .assertExists()
            .performClick()

        composeTestRule.onNodeWithContentDescription("Message")
            .assertExists()
    }

    @Test
    fun clickOnAddCardUnSuccessful() {
        val navController = TestNavHostController(ApplicationProvider.getApplicationContext())
        navController.navigatorProvider.addNavigator(ComposeNavigator())
        val dummyFlashCardDao = DummyFlashCardDaoUnsuccessfulInsert()
        val dummyNetworkService = DummyNetworkService()

        composeTestRule.setContent {
            AppNavigation(
                navController = navController,
                flashCardDao = dummyFlashCardDao,
                networkService = dummyNetworkService
            )
        }
        
        composeTestRule.onNodeWithContentDescription("navigateToAddCard")
            .performClick()

        composeTestRule.onNodeWithContentDescription("enTextField")
            .performTextInput("test")
        composeTestRule.onNodeWithContentDescription("vnTextField")
            .performTextInput("thử nghiệm")
        
        composeTestRule.onNodeWithContentDescription("Add")
            .assertExists()
            .performClick()

        composeTestRule.onNodeWithContentDescription("Message")
            .assertExists()
    }
}