package com.example.menuannam

/**
 * ============================================================
 * MAIN ACTIVITY - App Entry Point
 * ============================================================
 * Initializes all core services and sets up the Compose UI
 *
 * Responsibilities:
 * 1. Initialize Room database (FlashCardDatabase)
 * 2. Create Retrofit/OkHttpClient for API calls
 * 3. Set up DataStore for persistent preferences
 * 4. Initialize Navigation system
 * 5. Provide CoroutineScope for async operations
 *
 * Service Setup:
 * - Database: FlashCardDatabase.getDatabase() → singleton pattern
 * - Network: Retrofit with OkHttpClient (30s timeouts)
 * - DataStore: Stores EMAIL and TOKEN in app preferences
 * - Navigation: Type-safe Compose Navigation with NavController
 *
 * Data Flow:
 * 1. MainActivity creates all services
 * 2. Passes them to AppNavigation composable
 * 3. AppNavigation manages all screens and routing
 * 4. Screens use dao, networkService, and scope for operations
 * ============================================================
 */
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.navigation.compose.rememberNavController
import com.example.menuannam.data.database.FlashCardDatabase
import com.example.menuannam.data.network.NetworkService
import com.example.menuannam.presentation.navigation.AppNavigation
import com.example.menuannam.ui.theme.MenuAnNamTheme
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

val Context.dataStore by preferencesDataStore(name = "user_credentials")
val TOKEN = stringPreferencesKey("token")
val EMAIL = stringPreferencesKey("email")
/**
 * DataStore setup - Persistent key-value storage for user preferences
 * val dataStore: DataStore<Preferences> - initialized by delegation
 * val TOKEN: PreferencesKey<String> - stores authentication token
 * val EMAIL: PreferencesKey<String> - stores user email
 *
 * Accessed via: context.dataStore.data.collect() or context.dataStore.data.first()
 */

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            MenuAnNamTheme {
                val navController = rememberNavController()
                val appContext = applicationContext
                val scope = rememberCoroutineScope()

                val db = FlashCardDatabase.getDatabase(appContext)
                val flashCardDao = db.flashCardDao()

                // Create a single OkHttpClient instance with timeouts
                val sharedOkHttpClient = OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .build()

                // Create the Retrofit instance with the shared OkHttpClient
                val retrofit: Retrofit = Retrofit.Builder()
                    .baseUrl("https://placeholder.com")
                    .client(sharedOkHttpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()

                val networkService = retrofit.create(NetworkService::class.java)

                AppNavigation(
                    navController,
                    flashCardDao,
                    scope,
                    networkService
                )
            }
        }
    }
}








