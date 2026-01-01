package com.example.menuannam.data.network

import kotlinx.serialization.Serializable

@Serializable
data class UserCredential(val email: String)

@Serializable
data class TokenResponse(
    val code: Int,
    val message: String
)

@Serializable
data class AudioRequest(
    val word: String,
    val email: String,
    val token: String
)

@Serializable
data class AudioResponse(
    val code: Int,
    val message: String
)
