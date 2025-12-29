package com.example.menuannam.presentation.screens

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.example.menuannam.data.network.UserCredential
import com.example.menuannam.data.network.NetworkService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun LoginScreen(
    changeMessage: (String) -> Unit,
    networkService: NetworkService,
    navigateToToken: (String) -> Unit
) {
    var email by rememberSaveable { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        changeMessage("Please, introduce your email.")
    }

    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            modifier = Modifier
                .fillMaxWidth()
                .semantics { contentDescription = "emailTextField" },
            label = { Text("email") }
        )

        Button(
            modifier = Modifier
                .fillMaxWidth()
                .semantics { contentDescription = "Enter" },
            onClick = {
                scope.launch {
                    try {
                        val result = withContext(Dispatchers.IO) {
                            networkService.generateToken(
                                credential = UserCredential(email)
                            )
                        }
                        if (result.code == 200) {
                            changeMessage("Token sent to email: ${result.message}")
                            Log.d("FLASHCARD", "Token request successful: ${result.message}")
                            navigateToToken(email)
                        } else {
                            changeMessage("Error: ${result.message}")
                            Log.d("FLASHCARD", "Token request failed with code ${result.code}: ${result.message}")
                        }

                    } catch (e: Exception) {
                        changeMessage("There was an error in the token request.")
                        Log.d("FLASHCARD", "Unexpected exception: $e")
                    }
                }
            }
        ) {
            Text("Enter")
        }
    }
}
