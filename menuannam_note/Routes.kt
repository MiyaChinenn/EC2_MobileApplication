package com.example.menuannam

import kotlinx.serialization.Serializable

@Serializable
object HomeRoute

@Serializable
object StudyCardsRoute

@Serializable
object AddCardRoute

@Serializable
object SearchCardsRoute

@Serializable
object LoginRoute

@Serializable
data class TokenRoute(val email: String)

@Serializable
data class ShowCardRoute(val english: String, val vietnamese: String)

@Serializable
data class EditCardRoute(val english: String, val vietnamese: String)