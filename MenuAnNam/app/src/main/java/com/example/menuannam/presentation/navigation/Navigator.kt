package com.example.menuannam.presentation.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.toRoute
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.menuannam.data.database.FlashCardDao
import com.example.menuannam.data.entity.FlashCard
import com.example.menuannam.presentation.screens.MenuAnNam
import com.example.menuannam.presentation.screens.AddScreen
import com.example.menuannam.presentation.screens.StudyScreen
import com.example.menuannam.presentation.screens.SearchScreen
import com.example.menuannam.presentation.screens.ShowCardScreen
import com.example.menuannam.presentation.screens.LoginScreen
import com.example.menuannam.presentation.screens.TokenScreen
import com.example.menuannam.data.network.NetworkService
import com.example.menuannam.presentation.components.TopBarComponent
import com.example.menuannam.presentation.components.BottomBarComponent

/**
 * ============================================================
 * APP NAVIGATION - Central Navigation Hub
 * ============================================================
 * Controls all screen transitions and manages app state
 * Uses Compose Navigation with type-safe routes
 *
 * Architecture:
 * 1. NavHost - container for all composable screens
 * 2. Scaffold - top-level layout (TopBar + Content + BottomBar)
 * 3. composable<Route> - each route is a screen
 * 4. Shared state - message, flashCards updated across screens
 *
 * Type-Safe Routes vs String Routes:
 * - Compile-time error checking
 * - Automatic serialization of parameters
 * - Better IDE support
 * ============================================================
 */
@Composable
fun AppNavigation(
    navigation: NavHostController,      // NavController from MainActivity
    flashCardDao: FlashCardDao,         // Database access
    coroutineScope: CoroutineScope,     // For async operations (launched from MainActivity)
    networkService: NetworkService      // Retrofit service for API calls
) {
    // ===== STATE MANAGEMENT =====
    
    /**
     * Shared message across all screens
     * Updated by each screen to show status/feedback
     * Displayed in BottomBar
     */
    var message by remember { mutableStateOf("Welcome!") }
    val changeMessage: (String) -> Unit = { message = it }

    /**
     * All flashcards loaded at startup
     * Used by SearchScreen for list display
     * Updated when cards are added/deleted
     */
    var flashCards by remember { mutableStateOf(emptyList<FlashCard>()) }

    /**
     * LaunchedEffect runs once on composition
     * Loads all flashcards from database on app startup
     */
    LaunchedEffect(Unit) {
        coroutineScope.launch {
            flashCards = flashCardDao.getAll()
        }
    }

    // ===== DYNAMIC TITLE & BACK BUTTON =====
    
    /**
     * Get current route name from back stack
     * Determines what screen is currently displayed
     */
    val backStackEntry by navigation.currentBackStackEntryAsState()
    val routeName = backStackEntry?.destination?.route

    /**
     * Map route names to user-friendly titles
     * Displays in TopBar
     */
    val title = when (routeName) {
        HomeRoute::class.qualifiedName -> "Menu An Nam"
        StudyCardsRoute::class.qualifiedName -> "Study Cards"
        AddCardRoute::class.qualifiedName -> "Add Card"
        SearchCardsRoute::class.qualifiedName -> "Search Cards"
        LoginRoute::class.qualifiedName -> "Login"
        ShowCardRoute::class.qualifiedName -> "Flash Card"
        else -> "Menu An Nam"
    }

    /**
     * Show back button on all screens EXCEPT home
     * Only home doesn't have a back button (it's the start destination)
     */
    val showBack = routeName != HomeRoute::class.qualifiedName
    val navigateBack: () -> Unit = { navigation.navigateUp() }

    // ===== SCAFFOLD LAYOUT =====
    /**
     * Scaffold provides standard app structure:
     * - topBar: Title and back button
     * - content: NavHost with screen composition
     * - bottomBar: Status message display
     */
    Scaffold(
        topBar = {
            TopBarComponent(
                title = title,
                showBack = if (showBack) navigateBack else null
            )
        },
        bottomBar = {
            BottomBarComponent(message = message)
        }
    ) { innerPadding ->

        // ===== NAVIGATION HOST =====
        /**
         * NavHost manages all composable screens
         * startDestination = HomeRoute (app always starts at home)
         * Padding prevents overlap with TopBar/BottomBar
         */
        NavHost(
            navController = navigation,
            startDestination = HomeRoute,
            modifier = androidx.compose.ui.Modifier.padding(innerPadding)
        ) {

            // ============================================================
            // HOME ROUTE - Main menu
            // ============================================================
            composable<HomeRoute> {
                MenuAnNam(
                    onStudy = {
                        navigation.navigate(StudyCardsRoute)
                    },
                    onAdd = { navigation.navigate(AddCardRoute) },
                    onSearch = { navigation.navigate(SearchCardsRoute) },
                    onLogin = { navigation.navigate(LoginRoute) },
                    changeMessage = changeMessage
                )
            }

            // ============================================================
            // LOGIN ROUTE - Get email and request token
            // ============================================================
            composable<LoginRoute> {
                LoginScreen(
                    changeMessage = changeMessage,
                    networkService = networkService,
                    navigateToToken = { enteredEmail ->
                        navigation.navigate(TokenRoute(enteredEmail))
                    }
                )
            }

            // ============================================================
            // STUDY CARDS ROUTE - Interactive flashcard study session
            // ============================================================
            composable<StudyCardsRoute> {
                StudyScreen(
                    changeMessage = changeMessage,
                    flashCardDao = flashCardDao,
                    networkService = networkService,
                    coroutineScope = coroutineScope
                )
            }

            // ============================================================
            // ADD CARD ROUTE - Create new flashcard
            // ============================================================
            composable<AddCardRoute> {
                AddScreen(
                    changeMessage = changeMessage,
                    insertFlashCard = { card ->
                        coroutineScope.launch {
                            // Insert card (Room ignores if duplicate)
                            flashCardDao.insertAll(card)
                            // Reload all cards from database
                            flashCards = flashCardDao.getAll()
                            changeMessage("Added: ${card.englishCard}")
                        }
                    }
                )
            }

            // ============================================================
            // SEARCH CARDS ROUTE - Browse and manage existing cards
            // ============================================================
            composable<SearchCardsRoute> {
                SearchScreen(
                    flashCards = flashCards,
                    selectedItem = { card ->
                        navigation.navigate(
                            ShowCardRoute(
                                english = card.englishCard ?: "",
                                vietnamese = card.vietnameseCard ?: ""
                            )
                        )
                    }
                )
            }

            // ============================================================
            // SHOW CARD ROUTE - View single card and delete if desired
            // ============================================================
            /**
             * Receives parameters from SearchScreen (english, vietnamese)
             * Extracts route parameters using toRoute<ShowCardRoute>()
             * Creates FlashCard object for display
             */
            composable<ShowCardRoute> { backStackEntry ->
                val route = backStackEntry.toRoute<ShowCardRoute>()

                ShowCardScreen(
                    flashCard = FlashCard(
                        uid = 0,
                        englishCard = route.english,
                        vietnameseCard = route.vietnamese
                    ),
                    onDelete = { card ->
                        coroutineScope.launch {
                            // Delete from database using card content
                            flashCardDao.deleteFlashCard(
                                english = card.englishCard ?: "",
                                vietnamese = card.vietnameseCard ?: ""
                            )
                            // Reload all cards
                            flashCards = flashCardDao.getAll()
                            changeMessage("Deleted: ${card.englishCard}")
                            // Go back to search screen
                            navigation.navigateUp()
                        }
                    }
                )
            }

            // ============================================================
            // TOKEN ROUTE - Store token from login
            // ============================================================
            /**
             * Receives email parameter from LoginRoute
             * User enters token string received via email
             * Saves token to DataStore for later audio API calls
             */
            composable<TokenRoute> { backStackEntry ->
                val route = backStackEntry.toRoute<TokenRoute>()

                TokenScreen(
                    email = route.email,
                    changeMessage = changeMessage,
                    navigateToHome = {
                        navigation.navigate(HomeRoute) {
                            popUpTo(HomeRoute) { inclusive = true }
                        }
                    }
                )
            }
        }
    }
}