package com.example.menuannam

import android.content.Context
import java.io.File
import java.io.FileOutputStream
import java.security.MessageDigest

/**
 * Extension function to convert a String to its MD5 hash
 * Used for generating unique filenames for audio files
 * Example: "hello".toMd5() -> "5d41402abc4b2a76b9719d911017c592"
 */
fun String.toMd5(): String {
    val bytes = MessageDigest.getInstance("MD5").digest(this.toByteArray())
    return bytes.joinToString("") { "%02x".format(it) }
}

/**
 * Saves audio data (ByteArray from Base64 decoded API response) to internal storage
 * Creates an MP3 file in the app's private files directory
 *
 * @param context Android context to access filesDir
 * @param audioData The decoded audio bytes from API response
 * @param filename The desired filename (typically "{word.toMd5()}.mp3")
 * @return The created File object pointing to the saved audio
 *
 * Flow:
 * 1. Create a File object in context.filesDir (private app directory)
 * 2. Write audioData bytes to the file using FileOutputStream
 * 3. Return the File for later playback with ExoPlayer
 */
fun saveAudioToInternalStorage(context: Context, audioData: ByteArray, filename: String): File {
    val file = File(context.filesDir, filename)
    FileOutputStream(file).use { fos ->
        fos.write(audioData)
    }
    return file
}
