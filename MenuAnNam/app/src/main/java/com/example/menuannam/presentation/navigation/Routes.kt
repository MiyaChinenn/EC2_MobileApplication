package com.example.menuannam.presentation.navigation

import kotlinx.serialization.Serializable

// TYPE-SAFE NAVIGATION ROUTES: @Serializable objects for compile-time verification
// Kotlinx.serialization handles automatic type conversion for route parameters

@Serializable
object HomeRoute // Main menu: Study, Add Cards, Search, Login

@Serializable
object AddCardRoute // Create new flashcard with English/Vietnamese input

@Serializable
object StudyCardsRoute // Study 5 random cards with audio playback

@Serializable
data class SearchCardsRoute( // Display filtered search results with edit/delete options
    val englishText: String = "", // English search term
    val exactEnglish: Int = 0, // 1 for exact match, 0 for partial match
    val vietnameseText: String = "", // Vietnamese search term
    val exactVietnamese: Int = 0 // 1 for exact match, 0 for partial match
)

@Serializable
object LoginRoute // User enters email to get token for audio synthesis

@Serializable
data class ShowCardRoute(val id: Int) // View single flashcard with delete and audio playback

@Serializable
data class EditCardRoute(val id: Int) // Edit existing flashcard by ID

@Serializable
data class TokenRoute(val email: String) // Token input screen - email passed from LoginRoute for context

@Serializable
object FilterRoute // Filter search screen with English/Vietnamese terms and exact/partial match options