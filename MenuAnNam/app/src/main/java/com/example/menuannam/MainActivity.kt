package com.example.menuannam

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








