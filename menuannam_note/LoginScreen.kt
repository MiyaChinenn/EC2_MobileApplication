package com.example.menuannam

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    changeMessage: (String) -> Unit,
    networkService: NetworkService,
    navigateToToken: (String) -> Unit
) {
    var email by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        changeMessage("Login Screen")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        TextField(
            value = email,
            onValueChange = { email = it },
            modifier = Modifier
                .fillMaxWidth()
                .semantics { contentDescription = "emailTextField" },
            label = { Text("Email") }
        )

        Button(
            modifier = Modifier
                .fillMaxWidth()
                .semantics { contentDescription = "Enter" },
            onClick = {
                scope.launch {
                    try {
                        val response = networkService.generateToken(
                            email = UserCredential(email)
                        )

                        if (response.code == 200) {
                            changeMessage("Token sent to your email.")
                            navigateToToken(email)
                        } else {
                            changeMessage(response.message)
                        }

                    } catch (e: Exception) {
                        changeMessage("Network error.")
                        Log.e("LOGIN", "Error", e)
                    }
                }
            }
        ) {
            Text("Enter")
        }
    }
}

