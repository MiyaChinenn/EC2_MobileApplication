package com.example.menuannam.presentation.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign

/**
 * BottomBarComponent - Status message bar at bottom of screen
 * Displays real-time feedback to user (loading, success, error messages)
 *
 * Features:
 * - Shows dynamic messages updated by each screen
 * - BottomAppBar: Material 3 standard bottom bar
 * - Text centered for better visibility
 * - Semantic contentDescription for testing framework
 *
 * Message Examples:
 * - "Card 1 of 5" (in StudyScreen)
 * - "Logged out" (after logout)
 * - "Added: hello" (after successful card creation)
 * - "Playing audio..." (during audio synthesis)
 * - "Error loading flashcards: ..." (on database errors)
 *
 * Parameters:
 * @param message Current status message (updated by Navigator's changeMessage callback)
 */
@Composable
fun BottomBarComponent(
    message: String
) {
    BottomAppBar(){
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .semantics { contentDescription = "Message" },
            textAlign = TextAlign.Center,
            text = message
        )
    }
}