package com.example.menuannam

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuAnNam(
    onStudy: () -> Unit,
    onAdd: () -> Unit,
    onSearch: () -> Unit,
    onLogin: () -> Unit,
    changeMessage: (String) -> Unit = {}
) {
    val context = LocalContext.current
    val appContext = context.applicationContext
    val scope = rememberCoroutineScope()

    var email by remember { mutableStateOf("") }

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
        ) { Text("Add Card") }

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

