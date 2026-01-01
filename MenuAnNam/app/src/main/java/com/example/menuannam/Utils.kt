package com.example.menuannam

import android.content.Context
import java.io.File
import java.io.FileOutputStream
import java.security.MessageDigest

fun String.toMd5(): String {
    val bytes = MessageDigest.getInstance("MD5").digest(this.toByteArray())
    return bytes.joinToString("") { "%02x".format(it) }
}

fun saveAudioToInternalStorage(context: Context, audioData: ByteArray, filename: String): File {
    val file = File(context.filesDir, filename)
    FileOutputStream(file).use { fos ->
        fos.write(audioData)
    }
    return file
}
