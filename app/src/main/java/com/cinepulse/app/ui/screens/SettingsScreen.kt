package com.cinepulse.app.ui.screens

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.cinepulse.app.data.remote.RetrofitInstance
import com.cinepulse.app.data.remote.SettingsRequest
import com.cinepulse.app.security.SessionManager
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(
    sessionManager: SessionManager,
    onLogout: () -> Unit
) {
    var newDisplayName by remember { mutableStateOf(sessionManager.getDisplayName() ?: "") }
    var isLoading by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf<String?>(null) }
    var isError by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Settings",
            style = MaterialTheme.typography.headlineMedium
        )

        Text(text = "Signed in as: ${sessionManager.getEmail()}")

        OutlinedTextField(
            value = newDisplayName,
            onValueChange = { newDisplayName = it },
            label = { Text("Display name") },
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
        )

        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.padding(top = 16.dp))
        }

        message?.let {
            Text(
                text = it,
                color = if (isError) Color.Red else Color(0xFF2E7D32),
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        Button(
            onClick = {
                message = null
                if (newDisplayName.isBlank()) {
                    message = "Display name can't be empty"
                    isError = true
                    return@Button
                }

                isLoading = true
                scope.launch {
                    try {
                        val token = sessionManager.getToken()
                        val response = RetrofitInstance.backendApi.updateSettings(
                            bearerToken = "Bearer $token",
                            request = SettingsRequest(newDisplayName.trim())
                        )

                        sessionManager.saveSession(
                            token = token ?: "",
                            displayName = response.user.displayName,
                            email = response.user.email
                        )

                        message = "Settings saved!"
                        isError = false
                        Log.d("CinePulseSettings", "Updated to ${response.user.displayName}")

                    } catch (e: Exception) {
                        Log.e("CinePulseSettings", "Settings error", e)
                        message = e.message ?: "Failed to update settings"
                        isError = true
                    } finally {
                        isLoading = false
                    }
                }
            },
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
        ) {
            Text("Save settings")
        }

        OutlinedButton(
            onClick = onLogout,
            modifier = Modifier.fillMaxWidth().padding(top = 24.dp)
        ) {
            Text("Log out")
        }
    }
}