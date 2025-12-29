package com.example.menuannam.presentation.screens

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
import kotlinx.coroutines.launch
import com.example.menuannam.data.database.FlashCardDao
import com.example.menuannam.data.entity.FlashCard


@Composable
fun AddScreen(
    changeMessage: (String) -> Unit,
    flashCardDao: FlashCardDao
) {
    var enWord by rememberSaveable {mutableStateOf("")}
    var vnWord by rememberSaveable {mutableStateOf("")}
    val coroutineScope = rememberCoroutineScope()
    
    LaunchedEffect(Unit) {
        changeMessage("Please, add a flash card.")
    }

    Column(
        modifier = Modifier
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        OutlinedTextField(
            value = enWord,
            onValueChange = { enWord = it },
            modifier = Modifier
                .fillMaxWidth()
                .semantics { contentDescription = "enTextField" },
            label = { Text("en") }
        )
        OutlinedTextField(
            value = vnWord,
            onValueChange = { vnWord = it },
            modifier = Modifier
                .fillMaxWidth()
                .semantics { contentDescription = "vnTextField" },
            label = { Text("vn") }
        )
        Button(
            modifier = Modifier
                .fillMaxWidth()
                .semantics { contentDescription = "Add" },
            onClick = {
                coroutineScope.launch {
                    try {
                        flashCardDao.insert(
                            FlashCard(
                                uid = 0,
                                englishCard = enWord,
                                vietnameseCard = vnWord
                            )
                        )
                        enWord = ""
                        vnWord = ""
                        changeMessage("Card added successfully!")
                    } catch (e: Exception) {
                        changeMessage("Error: ${e.message}")
                    }
                }
            }
        ) {
            Text("Add")
        }
    }
}