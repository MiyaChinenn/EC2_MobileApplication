package com.example.menuannam.data.network

import kotlinx.serialization.Serializable

/**
 * UserCredential - Login request payload
 * Sent to Lambda token generation endpoint
 * @param email User email address for authentication
 */
@Serializable
data class UserCredential(val email: String)

/**
 * TokenResponse - Response from token generation Lambda
 * Indicates success/failure of token generation
 * @param code HTTP status code (200 = success)
 * @param message Response message or token string
 */
@Serializable
data class TokenResponse(
    val code: Int,
    val message: String
)

/**
 * AudioRequest - Audio generation request payload
 * Sent to Lambda audio synthesis endpoint
 * @param word The word/phrase to synthesize audio for
 * @param email User email (for API tracking)
 * @param token Authentication token from token endpoint
 */
@Serializable
data class AudioRequest(
    val word: String,
    val email: String,
    val token: String
)

/**
 * AudioResponse - Response from audio generation Lambda
 * Contains Base64-encoded MP3 audio data
 * @param code HTTP status code (200 = success)
 * @param message Base64-encoded audio string (for code 200) or error message
 */
)
