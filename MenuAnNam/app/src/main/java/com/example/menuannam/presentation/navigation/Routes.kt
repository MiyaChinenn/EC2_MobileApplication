package com.example.menuannam.presentation.navigation

import kotlinx.serialization.Serializable

/**
 * ============================================================
 * TYPE-SAFE NAVIGATION ROUTES
 * ============================================================
 * These @Serializable objects define all navigation destinations
 * NavHost uses these instead of string routes for compile-time safety
 * Kotlinx.serialization handles automatic serialization for route parameters
 *
 * Benefits:
 * - Compile-time verification (misspelled routes = compilation error)
 * - Automatic type conversion for parameters
 * - Better IDE support and refactoring
 * ============================================================
 */

/**
 * HomeRoute - Main menu screen
 * Shows options to: Study, Add Cards, Search, Login
 * No parameters needed
 */
@Serializable
object HomeRoute

/**
 * AddCardRoute - Screen to create new flashcard
 * Displays form for English and Vietnamese input
 * No parameters needed
 */
@Serializable
object AddCardRoute

/**
 * StudyCardsRoute - Screen for studying random flashcards
 * Loads 5 random cards in shuffled order
 * User taps to reveal Vietnamese and listen to audio
 * No parameters needed
 */
@Serializable
object StudyCardsRoute

/**
 * SearchCardsRoute - Screen to search and manage flashcards
 * Allows viewing, editing, deleting existing cards
 * No parameters needed
 */
@Serializable
object SearchCardsRoute

/**
 * LoginRoute - Authentication screen
 * User enters email to get token for audio synthesis
 * No parameters needed
 */
@Serializable
object LoginRoute

/**
 * ShowCardRoute - Screen to view single flashcard details
 * Displays both English and Vietnamese
 * Allows delete operation and audio playback
 * @param english English word/phrase (passed from SearchCardsRoute)
 * @param vietnamese Vietnamese translation (passed from SearchCardsRoute)
 */
@Serializable
data class ShowCardRoute(val english: String, val vietnamese: String)

/**
 * EditCardRoute - Screen to edit an existing flashcard
 * @param english Original English text (for lookup)
 * @param vietnamese Original Vietnamese text (for lookup)
 * New values are entered in UI and sent back to update
 */
@Serializable
data class EditCardRoute(val english: String, val vietnamese: String)

/**
 * TokenRoute - Token input screen
 * User enters token obtained from email login
 * @param email Email address (passed from LoginRoute for context)
 */
@Serializable
data class TokenRoute(val email: String)