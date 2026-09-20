package com.cinepulse.app

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.cinepulse.app.security.SessionManager
import com.cinepulse.app.ui.screens.AuthScreen
import com.cinepulse.app.ui.screens.HomeScreen
import com.cinepulse.app.ui.screens.SettingsScreen
import com.cinepulse.app.ui.screens.WatchlistScreen
import com.cinepulse.app.ui.theme.CinePulseTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CinePulseTheme {
                val sessionManager = remember { SessionManager(applicationContext) }
                var isLoggedIn by remember { mutableStateOf(sessionManager.isLoggedIn()) }
                var currentTab by remember { mutableStateOf("home") }

                if (isLoggedIn) {
                    Scaffold(
                        bottomBar = {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                Button(onClick = { currentTab = "home" }) {
                                    Text("Discover")
                                }
                                Button(onClick = { currentTab = "watchlist" }) {
                                    Text("Watchlist")
                                }
                                Button(onClick = { currentTab = "settings" }) {
                                    Text("Settings")
                                }
                            }
                        }
                    ) { padding ->
                        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
                            when (currentTab) {
                                "home" -> HomeScreen()
                                "watchlist" -> WatchlistScreen()
                                "settings" -> SettingsScreen(
                                    sessionManager = sessionManager,
                                    onLogout = {
                                        sessionManager.clearSession()
                                        Log.d("CinePulseAuth", "Logged out")
                                        isLoggedIn = false
                                    }
                                )
                            }
                        }
                    }
                } else {
                    AuthScreen(onAuthSuccess = {
                        Log.d("CinePulseAuth", "Auth succeeded")
                        isLoggedIn = true
                    })
                }
            }
        }
    }
}