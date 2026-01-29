package com.example.menuannam.data.network

import retrofit2.http.Body
import retrofit2.http.PUT
import retrofit2.http.Url

// Retrofit interface for AWS Lambda endpoints; default URLs overridden per call
interface NetworkService {
    // Token generation: sends email, returns token or error
    @PUT
    suspend fun generateToken(
        @Url url: String = "https://egsbwqh7kildllpkijk6nt4soq0wlgpe.lambda-url.ap-southeast-1.on.aws/",
        @Body email: UserCredential
    ): TokenResponse

    // Audio synthesis: sends Vietnamese word + token, returns Base64 MP3 or 500 if token expired
    @PUT
    suspend fun generateAudio(
        @Url url: String = "https://ityqwv3rx5vifjpyufgnpkv5te0ibrcx.lambda-url.ap-southeast-1.on.aws/",
        @Body request: AudioRequest
    ): AudioResponse
}
