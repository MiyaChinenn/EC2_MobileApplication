package com.example.menuannam.presentation.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import android.util.Base64
import java.io.File
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import com.example.menuannam.EMAIL
import com.example.menuannam.TOKEN
import com.example.menuannam.dataStore
import com.example.menuannam.toMd5
import com.example.menuannam.saveAudioToInternalStorage
import com.example.menuannam.getCachedAudioFile
import com.example.menuannam.data.database.FlashCardDao
import com.example.menuannam.data.entity.FlashCard
import com.example.menuannam.data.network.AudioRequest
import com.example.menuannam.data.network.NetworkService

// StudyScreen supports two modes: SINGLE_CARD (view one card) and STUDY_SESSION (interactive 3-card learning)
enum class CardViewMode {
    SINGLE_CARD, // View one specific card with delete button (from search)
    STUDY_SESSION // Interactive 3-card learning with flip and audio
}

@Composable
fun StudyScreen(
    changeMessage: (String) -> Unit = {}, // Updates status bar with feedback
    flashCardDao: FlashCardDao,
    networkService: NetworkService,
    mode: CardViewMode = CardViewMode.STUDY_SESSION,
    cardId: Int = 0, // Card ID for SINGLE_CARD mode
    onCardDeleted: () -> Unit = {}, // Navigate back after deletion
    coroutineScope: CoroutineScope? = null
) {
    val context = LocalContext.current
    val appContext = context.applicationContext
    val scope = coroutineScope ?: rememberCoroutineScope()

    var flashCard by remember { mutableStateOf<FlashCard?>(null) }
    var lesson by remember { mutableStateOf<List<FlashCard>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var currentIndex by remember { mutableIntStateOf(0) }
    var showVietnamese by remember { mutableStateOf(false) }

    // Load data based on mode: SINGLE_CARD loads one card, STUDY_SESSION loads 5 random cards
    LaunchedEffect(Unit) {
        scope.launch {
            try {
                isLoading = true
                when (mode) {
                    CardViewMode.SINGLE_CARD -> {
                        flashCard = flashCardDao.getById(cardId)
                        if (flashCard == null) {
                            errorMessage = "Flash card not found"
                            changeMessage("Flash card not found")
                        } else {
                            changeMessage("Viewing flash card details")
                        }
                    }
                    CardViewMode.STUDY_SESSION -> {
                        val allCards = flashCardDao.getAll()
                        lesson = if (allCards.size >= 3) {
                            allCards.shuffled().take(3)
                        } else {
                            allCards
                        }
                        if (lesson.isNotEmpty()) {
                            changeMessage("Card ${currentIndex + 1} of ${lesson.size}")
                        } else {
                            errorMessage = "No flashcards found"
                            changeMessage("No flashcards found")
                        }
                    }
                }
            } catch (e: Exception) {
                errorMessage = "Error loading data: ${e.message}"
                changeMessage("Error loading data")
            } finally {
                isLoading = false
            }
        }
    }

    // Play audio utility
    val playAudio: (String) -> Unit = { word ->
        scope.launch {
            try {
                if (word.isBlank()) {
                    changeMessage("Missing data to request audio")
                    return@launch
                }

                // Try cached audio first
                val cachedFile = getCachedAudioFile(appContext, word)
                if (cachedFile != null) {
                    val mediaItem = MediaItem.fromUri(cachedFile.absolutePath.toUri())
                    val player = ExoPlayer.Builder(appContext).build()
                    player.addListener(object : androidx.media3.common.Player.Listener {
                        override fun onPlaybackStateChanged(playbackState: Int) {
                            when (playbackState) {
                                androidx.media3.common.Player.STATE_BUFFERING -> changeMessage("Buffering...")
                                androidx.media3.common.Player.STATE_READY -> changeMessage("Playing...")
                                androidx.media3.common.Player.STATE_ENDED -> {
                                    player.release()
                                    changeMessage("Ready")
                                }
                                else -> {}
                            }
                        }
                    })
                    player.setMediaItem(mediaItem)
                    player.prepare()
                    player.play()
                    return@launch
                }

                // If no cache, call API to generate audio
                val prefs = appContext.dataStore.data.first()
                val email = prefs[EMAIL] ?: ""
                val token = prefs[TOKEN] ?: ""
                if (email.isBlank() || token.isBlank()) { // Missing authentication data
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
                    val file = saveAudioToInternalStorage(appContext, audioBytes, fileName) // Save for future use
                    val mediaItem = MediaItem.fromUri(file.absolutePath.toUri())
                    val player = ExoPlayer.Builder(appContext).build()
                    player.addListener(object : androidx.media3.common.Player.Listener { // ExoPlayer state callbacks
                        override fun onPlaybackStateChanged(playbackState: Int) {
                            when (playbackState) {
                                androidx.media3.common.Player.STATE_BUFFERING -> changeMessage("Buffering...")
                                androidx.media3.common.Player.STATE_READY -> changeMessage("Playing...")
                                androidx.media3.common.Player.STATE_ENDED -> {
                                    player.release()
                                    changeMessage("Ready")
                                }
                                else -> {}
                            }
                        }
                    })
                    player.setMediaItem(mediaItem)
                    player.prepare()
                    player.play()
                } else { // 500 = token invalid/expired
                    changeMessage("Audio error (${resp.code}): ${resp.message}")
                }
            } catch (e: Exception) {
                changeMessage("Audio error: ${e.message}")
            }
        }
    }

    // UI Layout
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (isLoading) {
            Text("Loading...")
        } else if (errorMessage != null) {
            Text(
                text = errorMessage!!,
                color = MaterialTheme.colorScheme.error
            )
        } else when (mode) {
            CardViewMode.SINGLE_CARD -> {
                // Single Card View
                if (flashCard != null) {
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

                    Row(
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Button(
                            onClick = {
                                scope.launch {
                                    try {
                                        flashCardDao.delete(flashCard!!)
                                        changeMessage("Card deleted successfully!")
                                        onCardDeleted()
                                    } catch (e: Exception) {
                                        changeMessage("Error: ${e.message}")
                                    }
                                }
                            }
                        ) {
                            Text("Delete")
                        }
                        Spacer(modifier = Modifier.size(8.dp))
                        Button(
                            onClick = {
                                playAudio(flashCard!!.vietnameseCard ?: "")
                            }
                        ) {
                            Text("Play Audio")
                        }
                    }
                }
            }

            CardViewMode.STUDY_SESSION -> {
                // Study Session View
                if (lesson.isNotEmpty()) {
                    val currentCard = lesson[currentIndex]

                    Text(
                        text = "Card ${currentIndex + 1} of ${lesson.size}",
                        style = MaterialTheme.typography.titleMedium
                    )

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .clickable {
                                showVietnamese = !showVietnamese
                            },
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (showVietnamese) {
                                    currentCard.vietnameseCard ?: ""
                                } else {
                                    currentCard.englishCard ?: ""
                                },
                                fontSize = 32.sp,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(32.dp)
                            )
                        }
                    }

                    if (showVietnamese) {
                        Button(
                            modifier = Modifier
                                .fillMaxWidth()
                                .semantics { contentDescription = "PlayAudio" },
                            onClick = {
                                playAudio(currentCard.vietnameseCard ?: "")
                            }
                        ) {
                            Text("Play Audio")
                        }
                    }

                    if (showVietnamese) {
                        Button(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = {
                                val nextIndex = (currentIndex + 1) % lesson.size
                                if (nextIndex == 0) {
                                    lesson = lesson.shuffled()
                                }
                                currentIndex = nextIndex
                                showVietnamese = false
                                changeMessage("Card ${currentIndex + 1} of ${lesson.size}")
                            }
                        ) {
                            Text("Next")
                        }
                    }
                }
            }
        }
    }
}