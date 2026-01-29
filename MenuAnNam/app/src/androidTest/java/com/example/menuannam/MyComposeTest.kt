//import androidx.compose.ui.test.DeviceConfigurationOverride
//import androidx.compose.ui.test.Locales
//import androidx.compose.ui.test.assert
//import androidx.compose.ui.test.assertIsDisplayed
//import androidx.compose.ui.test.assertTextEquals
//import androidx.compose.ui.test.hasText
//import androidx.compose.ui.test.junit4.createComposeRule
//
//import androidx.compose.ui.test.onNodeWithContentDescription
//import androidx.compose.ui.test.onNodeWithText
//import androidx.compose.ui.test.performClick
//import androidx.compose.ui.text.intl.LocaleList
//import androidx.navigation.NavController
//import androidx.navigation.compose.ComposeNavigator
//import androidx.navigation.compose.rememberNavController
//import androidx.navigation.testing.TestNavHostController
//import androidx.test.core.app.ApplicationProvider
//import com.example.menuannam.presentation.navigation.AppNavigation
//import com.example.menuannam.presentation.screens.AddScreen
//import junit.framework.TestCase.assertEquals
//
//
//import org.junit.Rule
//import org.junit.Test
//
//
//class MyComposeTest {
//
//
//
//    @get:Rule val composeTestRule = createComposeRule()
//    // use createAndroidComposeRule<YourActivity>() if you need access to
//    // an activity
//
//    @Test
//    fun myTestAddCard() {
//        // Start the app
//        composeTestRule.setContent {
//                //AppNavigation()
//        }
//        composeTestRule.onNodeWithText("Add").assertIsDisplayed()
//    }
//    @Test
//    fun myTestBackButtonAddScreen() {
//        // Start the app
//        composeTestRule.setContent {
//
//            //AppNavigation()
//
//        }
//
//        composeTestRule.onNodeWithText("Add").performClick()
//        composeTestRule.onNodeWithText("Back").assertIsDisplayed()
//        composeTestRule.onNodeWithText("Back").performClick()
//        composeTestRule.onNodeWithText("Add").assertIsDisplayed()
//
//    }
//
//    @Test
//    fun myTestHomeScreen() {
//        val navController = TestNavHostController(ApplicationProvider.getApplicationContext())
//        navController.navigatorProvider.addNavigator(ComposeNavigator())
//        composeTestRule.setContent {
//            AppNavigation(
//                navController,
//                insertFlashCard = {},
//                getAllFlashCards = { emptyList() },
//                getFlashCardById = { null },
//                deleteFlashCard = {},
//                updateFlashCard = {}
//            )
//        }
//        assertEquals("Main", navController.currentDestination?.route)
//    }
//
//    @Test
//    fun myTestStudyScreenButton() {
//        val navController = TestNavHostController(ApplicationProvider.getApplicationContext())
//        navController.navigatorProvider.addNavigator(ComposeNavigator())
//        composeTestRule.setContent {
//            AppNavigation(
//                navController,
//                insertFlashCard = {},
//                getAllFlashCards = { emptyList() },
//                getFlashCardById = { null },
//                deleteFlashCard = {},
//                updateFlashCard = {}
//            )
//        }
//        composeTestRule .onNodeWithContentDescription("navigationToStudyScreen")
//                        .assertExists()
//                        .assertTextEquals("Study")
//                        .performClick()
//
//        //composeTestRule.onNodeWithText("Study Screen").assertIsDisplayed()
//        assertEquals("Study", navController.currentDestination?.route)
//    }
//    @Test
//    fun myTestAddScreenButton() {
//        val navController = TestNavHostController(ApplicationProvider.getApplicationContext())
//        navController.navigatorProvider.addNavigator(ComposeNavigator())
//        composeTestRule.setContent {
//            AppNavigation(
//                navController,
//                insertFlashCard = {},
//                getAllFlashCards = { emptyList() },
//                getFlashCardById = { null },
//                deleteFlashCard = {},
//                updateFlashCard = {}
//            )
//        }
//        composeTestRule.onNodeWithText("Add").performClick()
//
//        //composeTestRule.onNodeWithText("Study Screen").assertIsDisplayed()
//        assertEquals("Add", navController.currentDestination?.route)
//    }
//    lateinit var navController: TestNavHostController
//    @Test
//    fun displayMessage() {
//        composeTestRule.setContent {
//            navController = TestNavHostController(ApplicationProvider.getApplicationContext())
//            navController.navigatorProvider.addNavigator(ComposeNavigator())
//            AppNavigation(
//                navController,
//                insertFlashCard = {},
//                getAllFlashCards = { emptyList() },
//                getFlashCardById = { null },
//                deleteFlashCard = {},
//                updateFlashCard = {}
//            )
//        }
//        // Navigate to the Add Card-screen
//        composeTestRule.runOnUiThread {
//            navController.navigate("Add")
//        }
//
//        composeTestRule.onNodeWithContentDescription("Message")
//            .assertExists()
//            .assert(hasText("Đây là bottom bar của add screen"))
//    }
//@Test
//fun viDisplayEmptyEnglish() {
//    composeTestRule.setContent {
//        DeviceConfigurationOverride(
//            DeviceConfigurationOverride.Locales(LocaleList("en"))
//        ) {
//            AddScreen(
//                changeMessage = {},
//                insertFlashCard = {}
//            )
//        }
//    }
//
//    composeTestRule.onNodeWithContentDescription("English Input")
//        .assertTextEquals("English", "")
//}
//
//}