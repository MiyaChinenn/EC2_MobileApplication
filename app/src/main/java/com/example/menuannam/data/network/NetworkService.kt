package com.example.menuannam.data.network

import retrofit2.http.Body
import retrofit2.http.PUT
import retrofit2.http.Url

interface NetworkService {
    @PUT
    suspend fun generateToken(
        @Url url: String = "https://egsbwqh7kildllpkijk6nt4soq0wlgpe.lambda-url.ap-southeast-1.on.aws/",
        @Body credential: UserCredential
    ): TokenResponse

    @PUT
    suspend fun generateAudio(
        @Url url: String = "https://ityqwv3rx5vifjpyufgnpkv5te0ibrcx.lambda-url.ap-southeast-1.on.aws/",
        @Body body: AudioRequest
    ): TokenResponse
}
