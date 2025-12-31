package com.example.menuannam.presentation.navigation

import kotlinx.serialization.Serializable

// Main Menu
@Serializable
object MainRoute

// Add Card
@Serializable
object AddRoute

// Study Cards
@Serializable
object StudyRoute

// Search Cards
@Serializable
object SearchRoute

// Login
@Serializable
object LoginRoute

// Token (takes email parameter)
@Serializable
data class TokenRoute(val email: String)

// Show Card (takes cardId parameter)
@Serializable
data class ShowCardRoute(val cardId: Int)

// Deprecated routes (kept for reference):
// @Serializable
// object HomeRoute
// @Serializable
// object AddCardRoute
// @Serializable
// object StudyCardsRoute
// @Serializable
// object SearchCardsRoute
// @Serializable
// data class ShowCardRoute(val english: String, val vietnamese: String)
// @Serializable
// data class EditCardRoute(val english: String, val vietnamese: String)