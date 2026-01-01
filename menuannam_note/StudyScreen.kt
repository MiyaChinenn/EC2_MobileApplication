package com.example.menuannam

import android.util.Base64
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.io.File

// IMPORTANT: If toMd5 and saveAudioToInternalStorage are in another file,
// ensure they are in the same package 'com.example.menuannam'
// or import them here.

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudyScreen(
    changeMessage: (String) -> Unit,
    flashCardDao: FlashCardDao,
    networkService: NetworkService,
    coroutineScope: CoroutineScope? = null
) {
    val context = LocalContext.current
    val scope = coroutineScope ?: rememberCoroutineScope()
    var lesson by remember { mutableStateOf<List<FlashCard>>(emptyList()) }
    var currentFlashCard by remember { mutableIntStateOf(0) }
    var currentLanguage by remember { mutableStateOf("EN") }

    var email by rememberSaveable() { mutableStateOf("") }
    var token by rememberSaveable { mutableStateOf("") }

    val player = remember { ExoPlayer.Builder(context).build() }

    LaunchedEffect(Unit) {
        context.dataStore.data.collect { preferences ->
            val savedEmail = preferences[EMAIL] ?: ""
            val savedToken = preferences[TOKEN] ?: ""

            if (savedEmail.isNotEmpty() && savedToken.isNotEmpty()) {
                email = savedEmail
                token = savedToken
                changeMessage("Logged in as $email")
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose { player.release() }
    }

    fun handleAudio(word: String) {
        // --- USING YOUR UTILS ---
        val fileName = "${word.toMd5()}.mp3"
        val file = File(context.filesDir, fileName)

        scope.launch {
            try {
                if (!file.exists()) {
                    if (email.isBlank() || token.isBlank()) {
                        changeMessage("Login required for audio")
                        return@launch
                    }

                    changeMessage("Downloading Audio...")
                    val response = networkService.generateAudio(
                        request = AudioRequest(word, email, token)
                    )

                    if (response.code == 200) {
                        // Decode Base64 string from API response
                        val audioBytes = Base64.decode(response.message, Base64.DEFAULT)

                        // --- USING YOUR UTILS ---
                        saveAudioToInternalStorage(context, audioBytes, fileName)
                    } else {
                        changeMessage("Server Error: ${response.message}")
                        return@launch
                    }
                }

                // Playback logic
                if (file.exists()) {
                    val mediaItem = MediaItem.fromUri(file.absolutePath.toUri())
                    player.setMediaItem(mediaItem)
                    player.prepare()
                    player.play()
                    changeMessage("Playing pronunciation")
                }

            } catch (e: Exception) {
                changeMessage("Audio error: ${e.localizedMessage}")
                e.printStackTrace()
            }
        }
    }

    LaunchedEffect(Unit) {
        changeMessage("Study Cards")
        lesson = flashCardDao.getLesson(3)
    }

    if (lesson.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No flashcards available.")
        }
        return
    }

    val card = lesson[currentFlashCard]

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = if (currentLanguage == "EN") card.englishCard ?: "" else card.vietnameseCard ?: "",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier
                .clickable {
                    currentLanguage = if (currentLanguage == "EN") "VI" else "EN"

                    // If switching to Vietnamese, try to play audio
                    if (currentLanguage == "VI") {
                        // Safe call: only play if vietnameseCard is not null
                        card.vietnameseCard?.let { handleAudio(it) }
                    }
                }
                .padding(24.dp)
        )

        if (currentLanguage == "VI") {
            Button(onClick = {
                if (currentFlashCard == lesson.lastIndex) {
                    lesson = lesson.shuffled()
                    currentFlashCard = 0
                } else {
                    currentFlashCard += 1
                }
                currentLanguage = "EN"
                changeMessage("Next card")
            }) {
                Text("Next")
            }
        }
    }
}