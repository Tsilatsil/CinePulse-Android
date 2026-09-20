package com.cinepulse.app.data.remote

data class RegisterRequest(
    val email: String,
    val password: String,
    val displayName: String
)

data class LoginRequest(
    val email: String,
    val password: String
)

data class AuthResponse(
    val token: String,
    val user: UserDto
)

data class UserDto(
    val id: String,
    val email: String,
    val displayName: String
)
data class SettingsRequest(
    val displayName: String
)

data class SettingsResponse(
    val user: UserDto
)