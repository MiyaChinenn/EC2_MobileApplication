package com.example.menuannam.presentation.screens

import android.util.Base64
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import com.example.menuannam.EMAIL
import com.example.menuannam.TOKEN
import com.example.menuannam.data.database.FlashCardDao
import com.example.menuannam.data.entity.FlashCard
import com.example.menuannam.data.network.AudioRequest
import com.example.menuannam.data.network.NetworkService
import com.example.menuannam.dataStore
import com.example.menuannam.saveAudioToInternalStorage
import com.example.menuannam.toMd5
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.io.File

@Composable
fun EditScreen(
    cardId: Int, // Flashcard ID to edit
    flashCardDao: FlashCardDao, // Database access
    networkService: NetworkService, // API for audio generation
    changeMessage: (String) -> Unit, // Status message callback
    onCardUpdated: () -> Unit // Navigate back after successful update
) {
    val context = LocalContext.current
    val appContext = context.applicationContext
    val coroutineScope = rememberCoroutineScope()

    var flashCard by remember { mutableStateOf<FlashCard?>(null) }
    var englishText by remember { mutableStateOf("") }
    var vietnameseText by remember { mutableStateOf("") }
    var audioExists by remember { mutableStateOf(false) }
    var audioFilePath by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(true) }

    // Load flashcard and check audio existence on screen load
    LaunchedEffect(cardId) {
        try {
            val card = flashCardDao.getById(cardId)
            if (card != null) {
                flashCard = card
                englishText = card.englishCard ?: ""
                vietnameseText = card.vietnameseCard ?: ""
                
                // Check if audio file exists for current Vietnamese word
                val vn = card.vietnameseCard ?: ""
                if (vn.isNotBlank()) {
                    val fileName = "${vn.toMd5()}.mp3"
                    val file = File(appContext.filesDir, fileName)
                    audioExists = file.exists()
                    audioFilePath = if (audioExists) file.absolutePath else ""
                }
                
                changeMessage("Please, edit the flashcard.")
            } else {
                changeMessage("Flash card not found")
            }
        } catch (e: Exception) {
            changeMessage("Error loading flash card: ${e.message}")
        } finally {
            isLoading = false
        }
    }

    if (isLoading) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Loading...")
        }
    } else if (flashCard == null) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Flash card not found",
                color = MaterialTheme.colorScheme.error
            )
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // English text field
            OutlinedTextField(
                value = englishText,
                onValueChange = { englishText = it },
                label = { Text("en") },
                modifier = Modifier
                    .fillMaxWidth()
                    .semantics { contentDescription = "enTextField" }
            )

            // Vietnamese text field
            OutlinedTextField(
                value = vietnameseText,
                onValueChange = { vietnameseText = it },
                label = { Text("vn") },
                modifier = Modifier
                    .fillMaxWidth()
                    .semantics { contentDescription = "vnTextField" }
            )

            // Audio file path display (if exists)
            if (audioExists) {
                OutlinedTextField(
                    value = audioFilePath,
                    onValueChange = {},
                    label = { Text("audio") },
                    readOnly = true,
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = false,
                    maxLines = 3
                )
            }

            Spacer(modifier = Modifier.size(4.dp))

            // Update flashcard button - saves changes to database
            Button(
                onClick = {
                    coroutineScope.launch {
                        try {
                            flashCardDao.update(
                                id = cardId,
                                english = englishText,
                                vietnamese = vietnameseText
                            )
                            changeMessage("Flash card updated successfully")
                            onCardUpdated()
                        } catch (e: Exception) {
                            changeMessage("Error updating flash card: ${e.message}")
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .semantics { contentDescription = "updateButton" }
            ) {
                Text("Update flashcard")
            }

            // Conditional audio buttons based on file existence
            if (audioExists) { // If audio exists: "Clean audio" and "Play audio"
                // Clean audio button - deletes audio file from storage
                Button(
                    onClick = {
                        coroutineScope.launch {
                            try {
                                val file = File(audioFilePath)
                                if (file.exists() && file.delete()) {
                                    audioExists = false
                                    audioFilePath = ""
                                    changeMessage("Audio deleted successfully")
                                } else {
                                    changeMessage("Failed to delete audio")
                                }
                            } catch (e: Exception) {
                                changeMessage("Error deleting audio: ${e.message}")
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .semantics { contentDescription = "cleanAudioButton" },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Clean audio")
                }

                // Play audio button
                Button(
                    onClick = {
                        coroutineScope.launch {
                            try {
                                changeMessage("Playing audio...")
                                val mediaItem = MediaItem.fromUri(audioFilePath.toUri())
                                val player = ExoPlayer.Builder(appContext).build()
                                player.addListener(object : androidx.media3.common.Player.Listener {
                                    override fun onPlaybackStateChanged(playbackState: Int) {
                                        when (playbackState) {
                                            androidx.media3.common.Player.STATE_BUFFERING -> 
                                                changeMessage("Buffering...")
                                            androidx.media3.common.Player.STATE_READY -> 
                                                changeMessage("Playing")
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
                            } catch (e: Exception) {
                                changeMessage("Error playing audio: ${e.message}")
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .semantics { contentDescription = "playAudioButton" }
                ) {
                    Text("Play audio")
                }
            } else { // If audio doesn't exist: "Generate audio"
                // Generate audio button - calls API to synthesize audio
                Button(
                    onClick = {
                        coroutineScope.launch {
                            try { // Load token and email from DataStore for audio API request
                                val prefs = appContext.dataStore.data.first()
                                val email = prefs[EMAIL] ?: ""
                                val token = prefs[TOKEN] ?: ""
                                val word = vietnameseText.trim()

                                if (word.isBlank() || email.isBlank() || token.isBlank()) { // Missing required data
                                    changeMessage("Missing data to request audio")
                                    return@launch
                                }

                                changeMessage("Generating audio...")
                                val resp = networkService.generateAudio( // Send Vietnamese word + token to AWS Lambda
                                    request = AudioRequest(word, email, token)
                                )
                                
                                if (resp.code == 200) { // Response code 200 = success
                                    val audioBytes = Base64.decode(resp.message, Base64.DEFAULT) // Decode Base64-encoded MP3
                                    val fileName = "${word.toMd5()}.mp3"
                                    val file = saveAudioToInternalStorage(appContext, audioBytes, fileName)
                                    audioExists = true
                                    audioFilePath = file.absolutePath
                                    changeMessage("Audio generated successfully")
                                } else { // 500 = token invalid/expired
                                    changeMessage("Audio generation failed (${resp.code}): ${resp.message}")
                                }
                            } catch (e: Exception) {
                                changeMessage("Error generating audio: ${e.message}")
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .semantics { contentDescription = "generateAudioButton" }
                ) {
                    Text("Generate audio")
                }
            }
        }
    }
}
