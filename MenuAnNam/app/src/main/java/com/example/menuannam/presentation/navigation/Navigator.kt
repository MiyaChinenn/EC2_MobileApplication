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
import com.example.menuannam.data.database.FlashCardDao
import com.example.menuannam.presentation.screens.MenuScreen
import com.example.menuannam.presentation.screens.AddScreen
import com.example.menuannam.presentation.screens.StudyScreen
import com.example.menuannam.presentation.screens.CardViewMode
import com.example.menuannam.presentation.screens.SearchScreen
import com.example.menuannam.presentation.screens.FilterScreen
import com.example.menuannam.presentation.screens.EditScreen
import com.example.menuannam.presentation.screens.LoginScreen
import com.example.menuannam.presentation.screens.TokenScreen
import com.example.menuannam.data.network.NetworkService
import com.example.menuannam.presentation.components.TopBarComponent
import com.example.menuannam.presentation.components.BottomBarComponent

// Central Navigation Hub: Controls all screen transitions and manages app state
// Uses Compose Navigation with type-safe routes for compile-time error checking
@Composable
fun AppNavigation(
    navigation: NavHostController, // NavController from MainActivity
    flashCardDao: FlashCardDao, // Database access
    coroutineScope: CoroutineScope, // For async operations (launched from MainActivity)
    networkService: NetworkService // Retrofit service for API calls
) {
    // Shared message across all screens - updated by each screen, displayed in BottomBar
    var message by remember { mutableStateOf("Welcome!") }
    val changeMessage: (String) -> Unit = { message = it }

    // Get current route name from back stack to determine what screen is currently displayed
    val backStackEntry by navigation.currentBackStackEntryAsState()
    val routeName = backStackEntry?.destination?.route

    // Map route names to user-friendly titles displayed in TopBar
    val title = when (routeName) {
        HomeRoute::class.qualifiedName -> "Menu An Nam"
        StudyCardsRoute::class.qualifiedName -> "Study Cards"
        AddCardRoute::class.qualifiedName -> "Add Card"
        SearchCardsRoute::class.qualifiedName -> "Search Results"
        FilterRoute::class.qualifiedName -> "Search Cards"
        LoginRoute::class.qualifiedName -> "Login"
        ShowCardRoute::class.qualifiedName -> "Flash Card"
        EditCardRoute::class.qualifiedName -> "Edit Card"
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
                MenuScreen(
                    onStudy = {
                        navigation.navigate(StudyCardsRoute)
                    },
                    onAdd = { navigation.navigate(AddCardRoute) },
                    onSearch = { navigation.navigate(FilterRoute) },
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
                    mode = CardViewMode.STUDY_SESSION,
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
                            changeMessage("Added: ${card.englishCard}")
                        }
                    }
                )
            }

            // ============================================================
            // SEARCH CARDS ROUTE - Display filtered search results
            // ============================================================
            composable<SearchCardsRoute> { backStackEntry ->
                val route = backStackEntry.toRoute<SearchCardsRoute>()
                SearchScreen(
                    changeMessage = changeMessage,
                    flashCardDao = flashCardDao,
                    onEdit = { cardId ->
                        navigation.navigate(EditCardRoute(cardId))
                    },
                    englishText = route.englishText,
                    exactEnglish = route.exactEnglish,
                    vietnameseText = route.vietnameseText,
                    exactVietnamese = route.exactVietnamese
                )
            }

            // ============================================================
            // FILTER ROUTE - Search input form
            // ============================================================
            composable<FilterRoute> {
                FilterScreen(
                    changeMessage = changeMessage,
                    onSearch = { en, exactEn, vn, exactVn ->
                        navigation.navigate(
                            SearchCardsRoute(
                                englishText = en,
                                exactEnglish = exactEn,
                                vietnameseText = vn,
                                exactVietnamese = exactVn
                            )
                        )
                    }
                )
            }

            // ============================================================
            // EDIT CARD ROUTE - Edit existing flashcard with audio management
            // ============================================================
            composable<EditCardRoute> { backStackEntry ->
                val route = backStackEntry.toRoute<EditCardRoute>()
                EditScreen(
                    cardId = route.id,
                    flashCardDao = flashCardDao,
                    networkService = networkService,
                    changeMessage = changeMessage,
                    onCardUpdated = { navigation.navigateUp() }
                )
            }

            // ============================================================
            // SHOW CARD ROUTE - View single card and delete if desired
            // ============================================================
            /**
             * Receives cardId from SearchScreen
             * Displays full card details with delete and play audio options
             */
            composable<ShowCardRoute> { backStackEntry ->
                val route = backStackEntry.toRoute<ShowCardRoute>()

                StudyScreen(
                    changeMessage = changeMessage,
                    flashCardDao = flashCardDao,
                    networkService = networkService,
                    mode = CardViewMode.SINGLE_CARD,
                    cardId = route.id,
                    onCardDeleted = { navigation.navigateUp() }
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