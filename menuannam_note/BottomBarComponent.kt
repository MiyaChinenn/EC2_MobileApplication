package com.example.menuannam

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign

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