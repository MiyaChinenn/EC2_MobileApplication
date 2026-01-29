package com.example.menuannam.data.network

import kotlinx.serialization.Serializable

// Request payloads and responses for Lambda API calls

@Serializable
data class UserCredential(val email: String) // Token generation request

@Serializable
data class TokenResponse(
    val code: Int, // HTTP status (200 = success)
    val message: String // Token string or error message
)

@Serializable
data class AudioRequest(
    val word: String, // Vietnamese word for audio synthesis
    val email: String, // User tracking
    val token: String // Auth token (expires after some time)
)

@Serializable
data class AudioResponse(
    val code: Int, // HTTP status (200 = success, 500 = token invalid/expired)
    val message: String // Base64-encoded MP3 or error message
)
