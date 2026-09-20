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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.cinepulse.app.data.remote.LoginRequest
import com.cinepulse.app.data.remote.RegisterRequest
import com.cinepulse.app.data.remote.RetrofitInstance
import com.cinepulse.app.security.SessionManager
import kotlinx.coroutines.launch

@Composable
fun AuthScreen(onAuthSuccess: () -> Unit) {
    var isRegisterMode by remember { mutableStateOf(false) }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var displayName by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "CinePulse",
            style = MaterialTheme.typography.headlineLarge
        )

        Text(text = if (isRegisterMode) "Create your account" else "Welcome back")

        if (isRegisterMode) {
            OutlinedTextField(
                value = displayName,
                onValueChange = { displayName = it },
                label = { Text("Display name") },
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
            )
        }

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
        )

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
        )

        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.padding(top = 16.dp))
        }

        errorMessage?.let {
            Text(text = it, color = Color.Red, modifier = Modifier.padding(top = 8.dp))
        }

        Button(
            onClick = {
                errorMessage = null

                if (email.isBlank() || password.isBlank() || (isRegisterMode && displayName.isBlank())) {
                    errorMessage = "Please fill in all fields"
                    return@Button
                }

                isLoading = true
                scope.launch {
                    try {
                        val response = if (isRegisterMode) {
                            RetrofitInstance.backendApi.register(
                                RegisterRequest(email.trim(), password, displayName.trim())
                            )
                        } else {
                            RetrofitInstance.backendApi.login(
                                LoginRequest(email.trim(), password)
                            )
                        }

                        sessionManager.saveSession(
                            token = response.token,
                            displayName = response.user.displayName,
                            email = response.user.email
                        )

                        Log.d("CinePulseAuth", "Session saved for ${response.user.displayName}")
                        onAuthSuccess()

                    } catch (e: Exception) {
                        Log.e("CinePulseAuth", "Auth error", e)
                        errorMessage = e.message ?: "Something went wrong"
                    } finally {
                        isLoading = false
                    }
                }
            },
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
        ) {
            Text(if (isRegisterMode) "Register" else "Login")
        }

        TextButton(onClick = { isRegisterMode = !isRegisterMode }) {
            Text(
                if (isRegisterMode) "Already have an account? Login"
                else "Don't have an account? Register"
            )
        }
    }
}