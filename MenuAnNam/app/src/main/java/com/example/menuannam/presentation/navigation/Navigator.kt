package com.example.menuannam.presentation.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.toRoute
import com.example.menuannam.data.database.FlashCardDao
import com.example.menuannam.presentation.navigation.MainRoute
import com.example.menuannam.presentation.navigation.AddRoute
import com.example.menuannam.presentation.navigation.StudyRoute
import com.example.menuannam.presentation.navigation.SearchRoute
import com.example.menuannam.presentation.navigation.LoginRoute
import com.example.menuannam.presentation.navigation.TokenRoute
import com.example.menuannam.presentation.navigation.ShowCardRoute
import kotlin.reflect.typeOf
import com.example.menuannam.presentation.screens.MenuAnNam
import com.example.menuannam.presentation.screens.AddScreen
import com.example.menuannam.presentation.screens.StudyScreen
import com.example.menuannam.presentation.screens.SearchScreen
import com.example.menuannam.presentation.screens.ShowCardScreen
import com.example.menuannam.presentation.screens.LoginScreen
import com.example.menuannam.presentation.screens.TokenScreen
import com.example.menuannam.data.network.NetworkService

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation(
    navController: NavHostController,
    flashCardDao: FlashCardDao,
    networkService: NetworkService
) {
    var message by remember { mutableStateOf("") }
    var currentEmail by remember { mutableStateOf("") }
    
    val navigateToAdd = fun() {
        navController.navigate(AddRoute)
    }
    val navigateToStudy = fun() {
        navController.navigate(StudyRoute)
    }
    val navigateToSearch = fun() {
        navController.navigate(SearchRoute)
    }
    val navigateToLogin = fun() {
        navController.navigate(LoginRoute)
    }
    val navigateToToken = fun(email: String) {
        currentEmail = email
        navController.navigate(TokenRoute(email))
    }
    val navigateToShowCard = fun(cardId: Int) {
        navController.navigate(ShowCardRoute(cardId))
    }
    val changeMessage = fun(text:String){
        message = text
    }

    Scaffold(
        topBar = {
            TopAppBar(
                colors = topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = {
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(
                            text = "Menu An Nam"
                        )
                    }
                },
                navigationIcon = {
                    val currentRoute =
                        navController.currentBackStackEntryAsState().value?.destination?.route
                    if (currentRoute != MainRoute::class.simpleName) {
                        Button(
                            modifier = Modifier.semantics{contentDescription="navigateBack"},
                            onClick = {
                                navController.navigateUp()
                            }) {
                            Text("Back")
                        }
                    }
                }
            )
        },
        bottomBar = {
            BottomAppBar(
                actions = {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .semantics {
                                contentDescription = "Message"
                            },
                        textAlign = TextAlign.Center,
                        text = message
                    )
                })
        }
    ) { innerPadding ->
        NavHost(
            modifier = Modifier.padding(innerPadding)
                .fillMaxWidth(),
            navController = navController,
            startDestination = MainRoute
        ) {
            // MAIN
            composable<MainRoute> {
                MenuAnNam(
                    changeMessage = changeMessage,
                    onAdd = navigateToAdd,
                    onStudy = navigateToStudy,
                    onSearch = navigateToSearch,
                    onLogin = navigateToLogin
                )
            }
            // ADD
            composable<AddRoute> {
                AddScreen(
                    changeMessage = changeMessage,
                    flashCardDao = flashCardDao
                )
            }
            // STUDY
            composable<StudyRoute> {
                StudyScreen(
                    changeMessage = changeMessage,
                    flashCardDao = flashCardDao,
                    networkService = networkService
                )
            }
            // SEARCH
            composable<SearchRoute> {
                SearchScreen(
                    changeMessage = changeMessage,
                    flashCardDao = flashCardDao,
                    selectedItem = navigateToShowCard
                )
            }
            // LOGIN
            composable<LoginRoute> {
                LoginScreen(
                    changeMessage = changeMessage,
                    networkService = networkService,
                    navigateToToken = navigateToToken
                )
            }
            // TOKEN
            composable<TokenRoute> { backStackEntry ->
                val route = backStackEntry.toRoute<TokenRoute>()
                TokenScreen(
                    email = route.email,
                    changeMessage = changeMessage,
                    navigateToHome = { receivedToken ->
                        currentEmail = route.email
                        changeMessage("Token received for $currentEmail")
                        navController.navigate(MainRoute) {
                            popUpTo(MainRoute) { inclusive = true }
                        }
                    }
                )
            }
            // SHOW CARD
            composable<ShowCardRoute> { backStackEntry ->
                val route = backStackEntry.toRoute<ShowCardRoute>()
                ShowCardScreen(
                    changeMessage = changeMessage,
                    cardId = route.cardId,
                    flashCardDao = flashCardDao,
                    networkService = networkService,
                    onCardDeleted = { navController.navigateUp() }
                )
            }
        }
    }
}