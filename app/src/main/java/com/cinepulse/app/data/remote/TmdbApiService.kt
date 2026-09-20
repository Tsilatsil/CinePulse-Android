package com.cinepulse.app.data.remote

import retrofit2.http.GET
import retrofit2.http.Query

data class TmdbSearchResponse(
    val page: Int,
    val results: List<TmdbMedia>
)

data class TmdbMedia(
    val id: Int,
    val title: String? = null,      // present for movies
    val name: String? = null,       // present for TV shows
    val overview: String?,
    val poster_path: String?,
    val vote_average: Double?,
    val media_type: String? = null
) {
    fun displayTitle(): String = title ?: name ?: "Untitled"
}

interface TmdbApiService {

    @GET("3/search/multi")
    suspend fun searchMulti(
        @Query("query") query: String,
        @Query("api_key") apiKey: String
    ): TmdbSearchResponse

    @GET("3/trending/all/week")
    suspend fun getTrending(
        @Query("api_key") apiKey: String
    ): TmdbSearchResponse
}