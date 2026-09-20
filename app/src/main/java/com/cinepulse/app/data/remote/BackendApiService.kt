package com.cinepulse.app.data.remote

import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST

interface BackendApiService {

    @POST("api/auth/register")
    suspend fun register(@Body request: RegisterRequest): AuthResponse

    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): AuthResponse

    @PATCH("api/v1/user/settings")
    suspend fun updateSettings(
        @Header("Authorization") bearerToken: String,
        @Body request: SettingsRequest
    ): SettingsResponse
}