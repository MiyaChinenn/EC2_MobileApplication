package com.example.menuannam.presentation.screens

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import com.example.menuannam.data.database.FlashCardDao
import com.example.menuannam.data.entity.FlashCard

@Composable
fun FlashCardList(
    selectedItem: (Int) -> Unit,
    flashCards: List<FlashCard>,
    onEdit: (FlashCard) -> Unit = {},
    onDelete: (FlashCard) -> Unit = {}
) {
    LazyColumn(
        modifier = Modifier.padding(16.dp)
    ) {
        items(
            items = flashCards,
            key = { flashCard ->
                flashCard.uid
            }
        ) { flashCard ->
            var isEditing by remember { mutableStateOf(false) }
            var editEnglish by remember { mutableStateOf(flashCard.englishCard ?: "") }
            var editVietnamese by remember { mutableStateOf(flashCard.vietnameseCard ?: "") }
            
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(width = 1.dp, color = Color.LightGray)
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (isEditing) {
                    Column(modifier = Modifier.weight(1f)) {
                        OutlinedTextField(
                            value = editEnglish,
                            onValueChange = { editEnglish = it },
                            label = { Text("en", fontSize = 12.sp) }
                        )
                        Spacer(modifier = Modifier.size(4.dp))
                        OutlinedTextField(
                            value = editVietnamese,
                            onValueChange = { editVietnamese = it },
                            label = { Text("vn", fontSize = 12.sp) }
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Button(
                            onClick = {
                                val updatedCard = flashCard.copy(
                                    englishCard = editEnglish,
                                    vietnameseCard = editVietnamese
                                )
                                onEdit(updatedCard)
                                isEditing = false
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                        ) {
                            Text("Save", fontSize = 12.sp)
                        }
                        Spacer(modifier = Modifier.size(4.dp))
                        Button(
                            onClick = {
                                editEnglish = flashCard.englishCard ?: ""
                                editVietnamese = flashCard.vietnameseCard ?: ""
                                isEditing = false
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                        ) {
                            Text("Cancel", fontSize = 12.sp)
                        }
                    }
                } else {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { selectedItem(flashCard.uid) }
                            .padding(6.dp)
                    ) {
                        Text("${flashCard.englishCard ?: ""} = ${flashCard.vietnameseCard ?: ""}")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Button(
                            onClick = { isEditing = true },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                        ) {
                            Text("Edit", fontSize = 12.sp)
                        }
                        Button(
                            onClick = { onDelete(flashCard) },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                        ) {
                            Text("Delete", fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun SearchScreen(
    changeMessage: (String) -> Unit = {},
    flashCardDao: FlashCardDao,
    selectedItem: (Int) -> Unit
) {
    var flashCards by remember { mutableStateOf<List<FlashCard>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    val coroutineScope = rememberCoroutineScope()

    val loadFlashCards = {
        coroutineScope.launch {
            try {
                flashCards = flashCardDao.getAll()
                isLoading = false
            } catch (e: Exception) {
                changeMessage("Error loading flash cards: ${e.message}")
                isLoading = false
            }
        }
    }

    LaunchedEffect(Unit) {
        changeMessage("Manage your flash cards.")
        loadFlashCards()
    }
    
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Spacer(
            modifier = Modifier.size(16.dp)
        )
        if (isLoading) {
            Text("Loading flash cards...")
        } else if (flashCards.isNotEmpty()) {
            FlashCardList(
                flashCards = flashCards,
                selectedItem = selectedItem,
                onEdit = { updatedCard ->
                    coroutineScope.launch {
                        try {
                            flashCardDao.update(
                                updatedCard.uid,
                                updatedCard.englishCard ?: "",
                                updatedCard.vietnameseCard ?: ""
                            )
                            changeMessage("Card updated successfully!")
                            loadFlashCards()
                        } catch (e: Exception) {
                            changeMessage("Error updating card: ${e.message}")
                        }
                    }
                },
                onDelete = { cardToDelete ->
                    coroutineScope.launch {
                        try {
                            flashCardDao.delete(cardToDelete)
                            changeMessage("Card deleted successfully!")
                            loadFlashCards()
                        } catch (e: Exception) {
                            changeMessage("Error deleting card: ${e.message}")
                        }
                    }
                }
            )
        } else {
            Text("No flash cards found. Add some cards first!")
        }
    }
}