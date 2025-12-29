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
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import com.example.menuannam.data.database.FlashCardDao
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
        navController.navigate("Add")
    }
    val navigateToStudy = fun() {
        navController.navigate("Study")
    }
    val navigateToSearch = fun() {
        navController.navigate("Search")
    }
    val navigateToLogin = fun() {
        navController.navigate("Login")
    }
    val navigateToToken = fun(email: String) {
        currentEmail = email
        navController.navigate("Token/$email")
    }
    val navigateToShowCard = fun(cardId: Int) {
        navController.navigate("ShowCard/$cardId")
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
                    if (currentRoute != "Main") {
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
            startDestination = "Main"
        ) {
            // MAIN
            composable(route = "Main") {
                MenuAnNam(
                    changeMessage = changeMessage,
                    onAdd = navigateToAdd,
                    onStudy = navigateToStudy,
                    onSearch = navigateToSearch,
                    onLogin = navigateToLogin
                )
            }
            // ADD
            composable(route = "Add") {
                AddScreen(
                    changeMessage = changeMessage,
                    flashCardDao = flashCardDao
                )
            }
            // STUDY
            composable(route = "Study") {
                StudyScreen(
                    changeMessage = changeMessage,
                    flashCardDao = flashCardDao,
                    networkService = networkService
                )
            }
            // SEARCH
            composable(route = "Search") {
                SearchScreen(
                    changeMessage = changeMessage,
                    flashCardDao = flashCardDao,
                    selectedItem = navigateToShowCard
                )
            }
            // LOGIN
            composable(route = "Login") {
                LoginScreen(
                    changeMessage = changeMessage,
                    networkService = networkService,
                    navigateToToken = navigateToToken
                )
            }
            // TOKEN
            composable(
                route = "Token/{email}",
                arguments = listOf(navArgument("email") { type = NavType.StringType })
            ) { backStackEntry ->
                val email = backStackEntry.arguments?.getString("email") ?: ""
                TokenScreen(
                    email = email,
                    changeMessage = changeMessage,
                    navigateToHome = { receivedToken ->
                        currentEmail = email
                        changeMessage("Token received for $currentEmail")
                        navController.navigate("Main") {
                            popUpTo("Main") { inclusive = true }
                        }
                    }
                )
            }
            // HOME removed: Menu screen now handles 'home' behavior
            // SHOW CARD
            composable(
                route = "ShowCard/{cardId}",
                arguments = listOf(navArgument("cardId") { type = NavType.IntType })
            ) { backStackEntry ->
                val cardId = backStackEntry.arguments?.getInt("cardId") ?: 0
                ShowCardScreen(
                    changeMessage = changeMessage,
                    cardId = cardId,
                    flashCardDao = flashCardDao,
                    networkService = networkService,
                    onCardDeleted = { navController.navigateUp() }
                )
            }
        }
    }
}