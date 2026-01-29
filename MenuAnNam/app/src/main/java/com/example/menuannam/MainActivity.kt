package com.example.menuannam

// Main entry point: initializes database, network, navigation, and DataStore for app
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.rememberCoroutineScope
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

// DataStore for persistent user credentials (email/token)
val Context.dataStore by preferencesDataStore(name = "user_credentials")
val TOKEN = stringPreferencesKey("token")
val EMAIL = stringPreferencesKey("email")

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            MenuAnNamTheme {
                val navController = rememberNavController()
                val appContext = applicationContext
                val scope = rememberCoroutineScope()

                // Database singleton for flashcard CRUD operations
                val db = FlashCardDatabase.getDatabase(appContext)
                val flashCardDao = db.flashCardDao()

                // HTTP client with 30s timeouts to prevent Lambda timeout issues
                val sharedOkHttpClient = OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .build()

                // Retrofit for Lambda API calls (token generation, audio synthesis)
                val retrofit: Retrofit = Retrofit.Builder()
                    .baseUrl("https://placeholder.com") // URL overridden per endpoint
                    .client(sharedOkHttpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()

                val networkService = retrofit.create(NetworkService::class.java)

                // Pass all services to navigation system
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








