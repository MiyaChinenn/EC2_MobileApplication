package com.example.menuannam.data.network

import retrofit2.http.Body
import retrofit2.http.PUT
import retrofit2.http.Url

/**
 * Retrofit interface for AWS Lambda API calls
 * All functions are suspend functions - run on IO thread via Retrofit's coroutine adapter
 *
 * Flow:
 * 1. Create request objects (UserCredential, AudioRequest)
 * 2. Call these functions from coroutine scope
 * 3. Retrofit serializes request to JSON with Gson
 * 4. Send HTTP PUT request to Lambda URLs
 * 5. Lambda returns JSON response
 * 6. Retrofit deserializes to response objects (TokenResponse, AudioResponse)
 */
interface NetworkService {
    /**
     * Generates authentication token from email
     * First step in workflow: user provides email -> get token
     *
     * Endpoint: Lambda function for token generation
     * @param url AWS Lambda URL (hardcoded default for security)
     * @param email UserCredential containing email string
     * @return TokenResponse with code (200 = success) and token in message field
     *
     * Flow:
     * 1. Send email to Lambda
     * 2. Lambda validates email and returns token
     * 3. Store token for audio generation later
     */
    @PUT
    suspend fun generateToken(
        @Url url: String = "https://egsbwqh7kildllpkijk6nt4soq0wlgpe.lambda-url.ap-southeast-1.on.aws/",
        @Body email: UserCredential): TokenResponse

    /**
     * Generates audio (MP3) for a word using text-to-speech
     * Second step in workflow: word + email + token -> audio file
     *
     * Endpoint: Lambda function for audio synthesis
     * @param url AWS Lambda URL (hardcoded default)
     * @param request AudioRequest with word, email, and token
     * @return AudioResponse with code (200 = success) and Base64-encoded MP3 in message field
     *
     * Flow:
     * 1. Prepare AudioRequest (word to speak, email, auth token)
     * 2. Send to Lambda for TTS synthesis
     * 3. Lambda returns Base64-encoded MP3 audio
     * 4. Client decodes Base64 -> byte array -> save to file -> play with ExoPlayer
     */
