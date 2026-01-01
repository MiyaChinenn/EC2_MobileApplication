package com.example.menuannam.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.compose.ui.platform.LocalContext
import android.util.Base64
import com.example.menuannam.EMAIL
import com.example.menuannam.TOKEN
import com.example.menuannam.dataStore
import com.example.menuannam.toMd5
import com.example.menuannam.saveAudioToInternalStorage
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.first
import com.example.menuannam.data.database.FlashCardDao
import com.example.menuannam.data.entity.FlashCard
import com.example.menuannam.data.network.AudioRequest
import com.example.menuannam.data.network.NetworkService
import java.io.File

/**
 * ShowCardScreen - View single flashcard with full details and options
 * Displays card contents (English and Vietnamese) and provides delete + audio playback
 *
 * Flow:
 * 1. Load card from database by ID (from ShowCardRoute parameter)
 * 2. Display loading state while fetching
 * 3. Show English and Vietnamese text in card format
 * 4. User can click "Play Audio" button to hear Vietnamese pronunciation
 * 5. User can click "Delete" button to remove card
 * 6. On delete, return to search screen (navigate back)
 *
 * Audio Feature:
 * - Gets email and token from DataStore
 * - Calls networkService.generateAudio(word, email, token)
 * - Lambda returns Base64-encoded MP3
 * - Decodes and saves using Utils functions (toMd5, saveAudioToInternalStorage)
 * - ExoPlayer handles playback with state callbacks
 *
 * State Management:
 * - isLoading: Shows loading spinner while fetching card
 * - errorMessage: Displays errors (card not found, load failed)
 * - Async card loading via LaunchedEffect
 *
 * Parameters:
 * @param changeMessage Updates status bar
 * @param cardId Unique ID of card to display
 * @param flashCardDao Database access for delete operation
 * @param networkService Retrofit for audio API
 * @param onCardDeleted Callback to return to search screen
 */
@Composable
fun ShowCardScreen(
    changeMessage: (String) -> Unit = {},
    cardId: Int,
    flashCardDao: FlashCardDao,
    networkService: NetworkService,
    onCardDeleted: () -> Unit = {}
) {
    var flashCard by remember { mutableStateOf<FlashCard?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(cardId) {
        try {
            isLoading = true
            flashCard = flashCardDao.getById(cardId)
            if (flashCard == null) {
                errorMessage = "Flash card not found"
                changeMessage("Flash card not found")
            } else {
                changeMessage("Viewing flash card details")
            }
        } catch (e: Exception) {
            errorMessage = "Error loading flash card: ${e.message}"
            changeMessage("Error loading flash card")
        } finally {
            isLoading = false
        }
    }

    val context = LocalContext.current
    val appContext = context.applicationContext

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (isLoading) {
            Text("Loading flash card...")
        } else if (errorMessage != null) {
            Text(
                text = errorMessage!!,
                color = MaterialTheme.colorScheme.error
            )
        } else if (flashCard != null) {
            // Flash Card Display
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Flash Card #${flashCard!!.uid}",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Spacer(modifier = Modifier.size(8.dp))
                    
                    // English Card
                    Text(
                        text = "English:",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = flashCard!!.englishCard ?: "N/A",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    
                    Spacer(modifier = Modifier.size(8.dp))
                    
                    // Vietnamese Card
                    Text(
                        text = "Vietnamese:",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = flashCard!!.vietnameseCard ?: "N/A",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
            
            // Delete Button
            Row(
                horizontalArrangement = Arrangement.Center
            ) {
                Button(
                    onClick = {
                        coroutineScope.launch {
                            try {
                                flashCardDao.delete(flashCard!!)
                                changeMessage("Flash card deleted successfully")
                                onCardDeleted()
                            } catch (e: Exception) {
                                changeMessage("Error deleting flash card: ${e.message}")
                            }
                        }
                    }
                ) {
                    Text("Delete")
                }
            }

            // Play Audio Button
            Row(
                horizontalArrangement = Arrangement.Center
            ) {
                Button(
                    onClick = {
                        coroutineScope.launch {
                            try {
                                val prefs = appContext.dataStore.data.first()
                                val email = prefs[EMAIL] ?: ""
                                val token = prefs[TOKEN] ?: ""
                                val word = flashCard!!.englishCard ?: ""
                                if (word.isBlank() || email.isBlank() || token.isBlank()) {
                                    changeMessage("Missing data to request audio")
                                    return@launch
                                }
                                val req = AudioRequest(word = word, email = email, token = token)
                                val resp = networkService.generateAudio(body = req)
                                if (resp.code == 200) {
                                    val audioBytes = Base64.decode(resp.message, Base64.DEFAULT)
                                    val fileName = "${word.toMd5()}.mp3"
                                    val file = saveAudioToInternalStorage(appContext, audioBytes, fileName)
                                    val mediaItem = MediaItem.fromUri(file.absolutePath.toUri())
                                    val player = ExoPlayer.Builder(appContext).build()
                                    player.addListener(object : androidx.media3.common.Player.Listener {
                                        override fun onPlaybackStateChanged(playbackState: Int) {
                                            when (playbackState) {
                                                androidx.media3.common.Player.STATE_BUFFERING -> changeMessage("Buffering...")
                                                androidx.media3.common.Player.STATE_READY -> changeMessage("Ready")
                                                androidx.media3.common.Player.STATE_ENDED -> {
                                                    player.release()
                                                    changeMessage("Finished")
                                                }
                                                else -> {}
                                            }
                                        }
                                    })
                                    player.setMediaItem(mediaItem)
                                    player.prepare()
                                    player.play()
                                } else {
                                    changeMessage(resp.message)
                                }
                            } catch (e: Exception) {
                                changeMessage("Audio error: ${e.message}")
                            }
                        }
                    }
                ) {
                    Text("Play Audio")
                }
            }
        }
    }
}