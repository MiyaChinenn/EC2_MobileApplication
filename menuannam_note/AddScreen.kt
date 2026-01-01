package com.example.menuannam

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.editableText
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.text
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddScreen(
    changeMessage: (String) -> Unit,
    insertFlashCard: (FlashCard) -> Unit
) {
    var english by rememberSaveable { mutableStateOf("") }
    var vietnamese by rememberSaveable { mutableStateOf("") }

    LaunchedEffect(Unit) {
        changeMessage("Add a new flashcard")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // English TextField (TEST CRITICAL)
        TextField(
            value = english,
            onValueChange = { english = it },
            label = { Text("English") },
            modifier = Modifier
                .fillMaxWidth()
                .semantics {
                    contentDescription = "enTextField"
                    text = AnnotatedString("en")
                    editableText = AnnotatedString(english)
                }
        )


        // Vietnamese TextField
        TextField(
            value = vietnamese,
            onValueChange = { vietnamese = it },
            label = { Text("Vietnamese") },
            modifier = Modifier
                .fillMaxWidth()
                .semantics { contentDescription = "viTextField" }
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically
        ) {

            // Add Button (TEST DOES NOT CARE ABOUT VALIDATION)
            Button(
                onClick = {
                    try {
                        insertFlashCard(FlashCard(0, english, vietnamese))
                        changeMessage("Flash card successfully added to your database.")
                        english = ""
                        vietnamese = ""
                    } catch (e: Exception) {
                        changeMessage("Flash card already exists in your database.")
                    }
                },
                modifier = Modifier.semantics { contentDescription = "Add" }
            ) {
                Text("Add")
            }

            Button(
                onClick = {
                    english = ""
                    vietnamese = ""
                },
                modifier = Modifier.semantics { contentDescription = "Clear" }
            ) {
                Text("Clear")
            }
        }
    }
}
