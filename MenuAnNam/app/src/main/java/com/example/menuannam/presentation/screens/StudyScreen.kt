package com.example.menuannam.presentation.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import android.util.Base64
import com.example.menuannam.EMAIL
import com.example.menuannam.TOKEN
import com.example.menuannam.dataStore
import com.example.menuannam.data.database.FlashCardDao
import com.example.menuannam.data.entity.FlashCard
import com.example.menuannam.data.network.AudioRequest
import com.example.menuannam.data.network.NetworkService
import java.io.File
import java.io.FileOutputStream
import java.security.MessageDigest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@Composable
fun StudyScreen(
    changeMessage: (String) -> Unit = {},
    flashCardDao: FlashCardDao,
    networkService: NetworkService
) {
    val context = LocalContext.current
    val appContext = context.applicationContext
    var lesson by remember { mutableStateOf<List<FlashCard>>(emptyList()) }
    var currentIndex by remember { mutableIntStateOf(0) }
    var showVietnamese by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        scope.launch {
            try {
                val allCards = flashCardDao.getAll()
                lesson = if (allCards.size >= 5) {
                    allCards.shuffled().take(5)
                } else {
                    allCards
                }
                changeMessage("Card ${currentIndex + 1} of ${lesson.size}")
            } catch (e: Exception) {
                changeMessage("Error loading flashcards: ${e.message}")
            }
        }
    }

    if (lesson.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("Loading flashcards...")
        }
    } else {
        val currentCard = lesson[currentIndex]
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically)
        ) {
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
                        scope.launch {
                            try {
                                val prefs = appContext.dataStore.data.first()
                                val email = prefs[EMAIL] ?: ""
                                val token = prefs[TOKEN] ?: ""
                                val word = currentCard.vietnameseCard ?: ""
                                if (word.isBlank() || email.isBlank() || token.isBlank()) {
                                    changeMessage("Missing data to request audio")
                                    return@launch
                                }
                                changeMessage("Playing audio...")
                                val resp = networkService.generateAudio(body = AudioRequest(word, email, token))
                                if (resp.code == 200) {
                                    val audioBytes = Base64.decode(resp.message, Base64.DEFAULT)
                                    val hash = MessageDigest.getInstance("MD5").digest(word.toByteArray()).joinToString("") { "%02x".format(it) }
                                    val file = File(appContext.filesDir, "$hash.mp3")
                                    FileOutputStream(file).use { it.write(audioBytes) }
                                    val mediaItem = MediaItem.fromUri(file.absolutePath.toUri())
                                    val player = ExoPlayer.Builder(appContext).build()
                                    player.addListener(object : androidx.media3.common.Player.Listener {
                                        override fun onPlaybackStateChanged(playbackState: Int) {
                                            when (playbackState) {
                                                androidx.media3.common.Player.STATE_BUFFERING -> changeMessage("Buffering")
                                                androidx.media3.common.Player.STATE_READY -> changeMessage("Playing")
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
                                } else {
                                    changeMessage("Audio error: ${resp.message}")
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
            
            if (showVietnamese) {
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        val nextIndex = (currentIndex + 1) % lesson.size
                        // Reshuffle when completing a cycle
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