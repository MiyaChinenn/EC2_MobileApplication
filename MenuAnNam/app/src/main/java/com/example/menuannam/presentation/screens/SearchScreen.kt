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
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import com.example.menuannam.data.database.FlashCardDao
import com.example.menuannam.data.entity.FlashCard

// FlashCardList component - LazyColumn displaying flashcards with edit/delete buttons
@Composable
fun FlashCardList(
    flashCards: List<FlashCard>,
    onEdit: (Int) -> Unit = {}, // Navigate to EditCardRoute with card ID
    onDelete: (FlashCard) -> Unit = {} // Delete card from database
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
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(width = 1.dp, color = Color.LightGray)
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(6.dp)
                ) {
                    Text("${flashCard.englishCard ?: ""} = ${flashCard.vietnameseCard ?: ""}")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Button(
                        onClick = { onEdit(flashCard.uid) },
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


@Composable
fun SearchScreen(
    changeMessage: (String) -> Unit = {},
    flashCardDao: FlashCardDao,
    onEdit: (Int) -> Unit = {},
    englishText: String = "",
    exactEnglish: Int = 0,
    vietnameseText: String = "",
    exactVietnamese: Int = 0
) {
    var filteredCards by remember { mutableStateOf<List<FlashCard>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    val performSearch = {
        coroutineScope.launch {
            try {
                isLoading = true
                filteredCards = flashCardDao.getFilteredFlashCards(englishText, exactEnglish, vietnameseText, exactVietnamese)
                changeMessage("Found ${filteredCards.size} cards")
                isLoading = false
            } catch (e: Exception) {
                changeMessage("Error: ${e.message}")
                isLoading = false
            }
        }
    }

    LaunchedEffect(Unit) {
        performSearch()
    }
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        // Results display
        if (isLoading) {
            Text("Searching...")
        } else if (filteredCards.isEmpty()) {
            Text("No cards found. Try different search terms.")
        } else {
            FlashCardList(
                flashCards = filteredCards,
                onEdit = onEdit,
                onDelete = { cardToDelete ->
                    coroutineScope.launch {
                        try {
                            flashCardDao.delete(cardToDelete)
                            changeMessage("Card deleted successfully!")
                            performSearch()
                        } catch (e: Exception) {
                            changeMessage("Error deleting card: ${e.message}")
                        }
                    }
                }
            )
        }
    }
}