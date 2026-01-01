package com.example.menuannam

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun ShowCardScreen(
    flashCard: FlashCard,
    onDelete: (FlashCard) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = flashCard.englishCard ?: "",
            style = MaterialTheme.typography.headlineMedium
        )

        Text(
            text = flashCard.vietnameseCard ?: "",
            style = MaterialTheme.typography.headlineMedium
        )

        Button(
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
            onClick = {
                onDelete(flashCard)
            }
        ) {
            Text("Delete")
        }
    }
}
