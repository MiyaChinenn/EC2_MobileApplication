package com.example.menuannam.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.edit
import com.example.menuannam.EMAIL
import com.example.menuannam.TOKEN
import com.example.menuannam.dataStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * TokenScreen - Confirm and save authentication token
 * Second step of login workflow: receive token from email, save to persistent storage
 *
 * Flow:
 * 1. User receives token string via email from Lambda
 * 2. User pastes/types token into TextField
 * 3. Click "Save Token" button
 * 4. Token is saved to DataStore (Preferences)
 * 5. Navigate back to HomeRoute
 * 6. Future audio requests use this token for authentication
 *
 * DataStore Persistence:
 * - TOKEN key stores authentication token
 * - EMAIL key stores user email (from LoginScreen)
 * - Used by StudyScreen and ShowCardScreen for audio requests
 * - Token enables AWS Lambda to authorize audio synthesis
 *
 * Parameters:
 * @param email User email (passed from LoginScreen, displayed as context)
 * @param changeMessage Updates status bar with feedback
 * @param navigateToHome Callback to return to main menu
 */
@Composable
fun TokenScreen(
    email: String,
    changeMessage: (String) -> Unit,
    navigateToHome: (String) -> Unit
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val appContext = context.applicationContext
    var token by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        changeMessage("Please, introduce your token.")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        OutlinedTextField(
            value = token,
            onValueChange = { token = it },
            modifier = Modifier
                .fillMaxWidth()
                .semantics { contentDescription = "tokenTextField" },
            label = { Text("token") }
        )
        OutlinedTextField(
            value = email,
            onValueChange = {},
            modifier = Modifier
                .fillMaxWidth()
                .semantics { contentDescription = "emailTextField" },
            label = { Text("email") },
            readOnly = true
        )
        Button(
            modifier = Modifier
                .fillMaxWidth()
                .semantics { contentDescription = "Enter" },
            onClick = {
                scope.launch {
                    withContext(Dispatchers.IO) {
                        appContext.dataStore.edit { preferences ->
                            preferences[EMAIL] = email
                            preferences[TOKEN] = token
                        }
                    }
                    navigateToHome(token)
                }
            }
        ) {
            Text("Enter")
        }
    }
}
