package com.example.menuannam

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Text
import androidx.compose.runtime.rememberCoroutineScope
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.navigation.compose.rememberNavController
import androidx.room.Room
//import com.mobile.teachingmobile26.ui.
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

//The Preferences DataStore implementation uses the DataStore and Preferences classes to persist key-value pairs to disk.
//Use the property delegate created by preferencesDataStore to create an instance of DataStore<Preferences>.
//Call it once at the top level of your Kotlin file. Access DataStore through this property
//throughout the rest of your application. This makes it easier to keep your DataStore as a singleton.
val Context.dataStore by preferencesDataStore(
    name = "user_credentials"
)

//Because Preferences DataStore doesn't use a predefined schema,
//you must use the corresponding key type function to define a key for each value that you need to store
//in the DataStore<Preferences> instance.
//For example, to define a key for an int value, use intPreferencesKey()
val TOKEN = stringPreferencesKey("token")
val EMAIL = stringPreferencesKey("email")


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            val appContext = applicationContext
            val scope = rememberCoroutineScope()
            // database
            val db = FlashCardDatabase.getDatabase(appContext)
            //Room.databaseBuilder(
            //appContext,
            //FlashCardDatabase::class.java, "FlashCardDatabase"
            //).build()

            val flashCardDao = db.flashCardDao()

            // network
            // Create a single OkHttpClient instance
            val sharedOkHttpClient = OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS) // Set connection timeout
                .readTimeout(30, TimeUnit.SECONDS)    // Set read timeout
                .build()


            // 2. Create the first Retrofit instance, using the shared OkHttpClient
            //.client(sharedOkHttpClient) // Pass the shared client

            // Retrofit requires a valid HttpUrl: The baseUrl() method of Retrofit.Builder expects an okhttp3.HttpUrl object.
            // This object represents a well-formed URL and requires a scheme (like "http" or "https"),
            // a host, and optionally a port and path. It cannot be null or an empty string.
            // You can use a placeholder or dummy URL, such as http://localhost/ or http://example.com/,
            // during the initial setup. This satisfies Retrofit's requirement for a valid base URL.

            val retrofit: Retrofit = Retrofit.Builder()
                .baseUrl("https://placeholder.com")
                .client(sharedOkHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            // Create an implementation of the API endpoints defined by the service interface.
            val networkService = retrofit.create(NetworkService::class.java)


            AppNavigation(navController, flashCardDao,scope,networkService)
        }
    }
}
/*
 * ===========================================================================================
 * LOGICAL DATA FLOW & PERSISTENCE ARCHITECTURE: MENU AN NAM
 * ===========================================================================================
 *
 * PHASE 1: APP STARTUP (Cold Start)
 * -------------------------------------------------------------------------------------------
 * 1. MainActivity initializes the Singleton FlashCardDatabase (Room) and NetworkService.
 * 2. AppNavigation triggers a 'LaunchedEffect(Unit)'.
 * 3. DATA MOVEMENT: Room Database (Disk) -> 'flashCards' List (RAM/Memory).
 * 4. PURPOSE: Pre-loading cards ensures the Search screen is instant and "Search Cards"
 * doesn't lag during the first use.
 *
 * PHASE 2: LOGIN & EMAIL CAPTURE (Transient State)
 * -------------------------------------------------------------------------------------------
 * 1. User enters email in LoginScreen.
 * 2. VARIABLE: 'enteredEmail' lives in local RAM.
 * 3. NAVIGATION: Navigator uses 'TokenRoute(enteredEmail)'.
 * 4. LOGIC: The email is "passed in an envelope" through the Navigation Backstack.
 * It is NOT saved permanently yet. It is just traveling between screens.
 *
 * PHASE 3: THE TOKEN HANDSHAKE (Permanent Persistence)
 * -------------------------------------------------------------------------------------------
 * 1. User retrieves token from their physical email and types it into TokenScreen.
 * 2. ACTION: User clicks "Enter".
 * 3. DATA SAVING: 'appContext.dataStore.edit' writes both EMAIL and TOKEN to the phone disk.
 * 4. STORAGE: Jetpack DataStore (Disk).
 * 5. CLEANUP: 'popUpTo(HomeRoute) { inclusive = true }' wipes the Login/Token screens
 * from RAM. The transient 'enteredEmail' variable dies, but the permanent keys are now safe.
 *
 * PHASE 4: THE HOME HUB (State Verification)
 * -------------------------------------------------------------------------------------------
 * 1. User arrives at MenuAnNam (Home).
 * 2. LOGIC: 'LaunchedEffect' reads the DataStore Flow.
 * 3. FEEDBACK: 'changeMessage' updates the BottomBar message.
 * 4. STATE: The app is now "Stateless" in navigation but "Aware" of the user identity
 * via the DataStore file on disk.
 *
 * PHASE 5: STUDY & AUDIO LOGIC (The Consumer Pattern)
 * -------------------------------------------------------------------------------------------
 * 1. Navigator opens StudyScreen with ZERO parameters (StudyCardsRoute is a simple object).
 * 2. CREDENTIAL RECOVERY: StudyScreen reads the EMAIL and TOKEN directly from DataStore (Disk).
 * 3. AUDIO CACHING:
 * a. Screen calculates MD5 hash of the Vietnamese word (Deterministic Filename).
 * b. It checks 'context.filesDir' (Disk) for "word_hash.mp3".
 * c. IF MISSING: Uses saved TOKEN/EMAIL to call API -> Downloads Bytes -> Saves to Disk.
 * d. IF PRESENT: Loads directly into ExoPlayer.
 * 4. PERSISTENCE: Audio stays on the phone even if the user logs out or goes offline.
 *
 * -------------------------------------------------------------------------------------------
 * SUMMARY OF STORAGE TYPES:
 * - Room (SQLite): Permanent Flashcards.
 * - DataStore: Permanent User Session (Email/Token).
 * - Internal Storage: Permanent Audio Files (.mp3).
 * - Navigation Routes: Temporary Handoffs (Email).
 * - mutableStateOf: Temporary UI states (Message, Current Card Index).
 * ===========================================================================================
 */