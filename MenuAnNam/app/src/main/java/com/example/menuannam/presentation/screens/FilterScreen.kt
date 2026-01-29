package com.example.menuannam.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp

@Composable
fun FilterScreen(
    changeMessage: (String) -> Unit = {}, // Updates status bar with feedback
    onSearch: (en: String, exactEn: Int, vn: String, exactVn: Int) -> Unit // Navigate to SearchCardsRoute with parameters
) {
    var englishText by remember { mutableStateOf("") } // English search term
    var vietnameseText by remember { mutableStateOf("") } // Vietnamese search term
    var exactEnglish by remember { mutableStateOf(false) } // Exact match checkbox for English
    var exactVietnamese by remember { mutableStateOf(false) } // Exact match checkbox for Vietnamese

    LaunchedEffect(Unit) {
        changeMessage("Enter search criteria and click Search")
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // English search field with checkbox
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = englishText,
                onValueChange = { englishText = it },
                label = { Text("English") },
                modifier = Modifier
                    .weight(1f)
                    .semantics { contentDescription = "enSearchField" },
                singleLine = true
            )
            Checkbox(
                checked = exactEnglish,
                onCheckedChange = { exactEnglish = it },
                modifier = Modifier.semantics { contentDescription = "enExactCheckbox" }
            )
        }

        // Vietnamese search field with checkbox
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = vietnameseText,
                onValueChange = { vietnameseText = it },
                label = { Text("Vietnamese") },
                modifier = Modifier
                    .weight(1f)
                    .semantics { contentDescription = "vnSearchField" },
                singleLine = true
            )
            Checkbox(
                checked = exactVietnamese,
                onCheckedChange = { exactVietnamese = it },
                modifier = Modifier.semantics { contentDescription = "vnExactCheckbox" }
            )
        }

        Spacer(modifier = Modifier.size(16.dp))

        // Search button
        Button(
            onClick = {
                val en = englishText.trim().ifEmpty { "" }
                val vn = vietnameseText.trim().ifEmpty { "" }
                val exactEn = if (exactEnglish) 1 else 0
                val exactVn = if (exactVietnamese) 1 else 0

                changeMessage("Searching...")
                onSearch(en, exactEn, vn, exactVn)
            },
            modifier = Modifier
                .fillMaxWidth()
                .semantics { contentDescription = "searchButton" }
        ) {
            Text("Search")
        }

        // Clear button
        Button(
            onClick = {
                englishText = ""
                vietnameseText = ""
                exactEnglish = false
                exactVietnamese = false
                changeMessage("Search criteria cleared")
            },
            modifier = Modifier
                .fillMaxWidth()
                .semantics { contentDescription = "clearButton" }
        ) {
            Text("Clear")
        }
    }
}
