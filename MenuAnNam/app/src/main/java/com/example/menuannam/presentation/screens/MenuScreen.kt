package com.example.menuannam.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.edit
import com.example.menuannam.EMAIL
import com.example.menuannam.TOKEN
import com.example.menuannam.dataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuScreen(
    onStudy: () -> Unit, // Navigate to StudyCardsRoute
    onAdd: () -> Unit, // Navigate to AddCardRoute
    onSearch: () -> Unit, // Navigate to SearchCardsRoute
    onLogin: () -> Unit, // Navigate to LoginRoute
    changeMessage: (String) -> Unit = {} // Update bottom status bar
) {
    val context = LocalContext.current
    val appContext = context.applicationContext
    val scope = rememberCoroutineScope()

    var email by remember { mutableStateOf("") }

    // Load saved email from DataStore on screen load - shows user they are logged in if email stored
    LaunchedEffect(Unit) {
        val prefs = appContext.dataStore.data.first()
        email = prefs[EMAIL] ?: ""
        changeMessage("Email loaded: $email")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Button(
            onClick = onStudy,
            modifier = Modifier.fillMaxWidth()
                .semantics { contentDescription = "navigateToStudyCards" }
        ) { Text("Study Cards") }

        Button(
            onClick = onAdd,
            modifier = Modifier.fillMaxWidth()
                .semantics { contentDescription = "navigateToAddCard" }
        ) { Text("Add Cards") }

        Button(
            onClick = onSearch,
            modifier = Modifier.fillMaxWidth()
                .semantics { contentDescription = "navigateToSearchCards" }
        ) { Text("Search Cards") }

        Button(
            onClick = onLogin,
            modifier = Modifier.fillMaxWidth()
                .semantics { contentDescription = "navigateToLoginScreen" }
        ) { Text("Login") }

        Button(
            onClick = {
                scope.launch {
                    appContext.dataStore.edit {
                        it.remove(EMAIL)
                        it.remove(TOKEN)
                    }
                    email = ""
                    changeMessage("Logged out")
                }
            },
            modifier = Modifier.fillMaxWidth()
                .semantics { contentDescription = "ExecuteLogout" }
        ) {
            Text("Log out", modifier = Modifier.semantics { contentDescription = "Logout" })
        }
    }
}