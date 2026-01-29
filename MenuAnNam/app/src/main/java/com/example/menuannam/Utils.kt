package com.example.menuannam

import android.content.Context
import java.io.File
import java.io.FileOutputStream
import java.security.MessageDigest

// Convert string to MD5 hash for unique audio filenames (e.g., "hello" → "5d41402abc...")
fun String.toMd5(): String {
    val bytes = MessageDigest.getInstance("MD5").digest(this.toByteArray())
    return bytes.joinToString("") { "%02x".format(it) }
}

// Save Base64-decoded audio bytes to app's private storage as MP3
fun saveAudioToInternalStorage(context: Context, audioData: ByteArray, filename: String): File {
    val file = File(context.filesDir, filename) // Private app directory
    FileOutputStream(file).use { fos ->
        fos.write(audioData)
    }
    return file // Return File for ExoPlayer playback
}

// Check if audio file exists in cache before making API call
fun getCachedAudioFile(context: Context, word: String): File? {
    val file = File(context.filesDir, "${word.toMd5()}.mp3")
    return if (file.exists()) file else null
}
