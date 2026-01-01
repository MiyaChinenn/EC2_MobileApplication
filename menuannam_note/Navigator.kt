package com.example.menuannam

import SearchCardsScreen
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import androidx.navigation.toRoute

@Composable
fun AppNavigation(
    navigation: NavHostController,
    flashCardDao: FlashCardDao,
    coroutineScope: CoroutineScope,
    networkService: NetworkService
) {
    var message by remember { mutableStateOf("Welcome!") }
    val changeMessage: (String) -> Unit = { message = it }

    var flashCards by remember { mutableStateOf(emptyList<FlashCard>()) }

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            flashCards = flashCardDao.getAll()
        }
    }

    val backStackEntry by navigation.currentBackStackEntryAsState()
    val routeName = backStackEntry?.destination?.route

    val title = when (routeName) {
        HomeRoute::class.qualifiedName -> "Menu An Nam"
        StudyCardsRoute::class.qualifiedName -> "Study Cards"
        AddCardRoute::class.qualifiedName -> "Add Card"
        SearchCardsRoute::class.qualifiedName -> "Search Cards"
        LoginRoute::class.qualifiedName -> "Login"
        ShowCardRoute::class.qualifiedName -> "Flash Card"
        else -> "Menu An Nam"
    }

    val showBack = routeName != HomeRoute::class.qualifiedName
    val navigateBack: () -> Unit = { navigation.navigateUp() }

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

        NavHost(
            navController = navigation,
            startDestination = HomeRoute,
            modifier = Modifier.padding(innerPadding)
        ) {

            composable<HomeRoute> {
                MenuAnNam(
                    onStudy = {
                        // Pass the current state variables.
                        // If user hasn't logged in, these will be ""
                        navigation.navigate(StudyCardsRoute)
                    },
                    onAdd = { navigation.navigate(AddCardRoute) },
                    onSearch = { navigation.navigate(SearchCardsRoute) },
                    onLogin = { navigation.navigate(LoginRoute) },
                    changeMessage = changeMessage
//                    networkService = networkService
                )
            }
            composable<LoginRoute> {
                LoginScreen(
                    changeMessage = changeMessage,
                    networkService = networkService,
                    navigateToToken = { enteredEmail ->
                        // Use the email provided by the LoginScreen directly in the route
                        navigation.navigate(TokenRoute(enteredEmail))
                    }
                )
            }


            composable<StudyCardsRoute> { backStackEntry ->
                StudyScreen(
                    changeMessage = changeMessage,
                    flashCardDao = flashCardDao,
                    networkService = networkService,
                    coroutineScope = coroutineScope
                )
            }

            composable<AddCardRoute> {
                AddScreen(
                    changeMessage = changeMessage,
                    insertFlashCard = { card ->
                        coroutineScope.launch {
                            flashCardDao.insertAll(card)
                            flashCards = flashCardDao.getAll()
                            changeMessage("Added: ${card.englishCard}")
                        }
                    }
                )
            }

            composable<SearchCardsRoute> {
                SearchCardsScreen(
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
                            flashCardDao.deleteFlashCard(
                                english = card.englishCard ?: "",
                                vietnamese = card.vietnameseCard ?: ""
                            )
                            flashCards = flashCardDao.getAll()
                            changeMessage("Deleted: ${card.englishCard}")
                            navigation.navigateUp()
                        }
                    }
                )
            }

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


