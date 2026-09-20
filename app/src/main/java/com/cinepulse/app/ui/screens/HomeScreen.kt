package com.cinepulse.app.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.cinepulse.app.BuildConfig
import com.cinepulse.app.data.local.AppDatabase
import com.cinepulse.app.data.local.WatchlistItem
import com.cinepulse.app.data.remote.RetrofitInstance
import com.cinepulse.app.data.remote.TmdbMedia
import kotlinx.coroutines.launch

@Composable
fun HomeScreen() {
    var query by remember { mutableStateOf("") }
    var results by remember { mutableStateOf<List<TmdbMedia>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val db = remember { AppDatabase.getInstance(context) }

    LaunchedEffect(Unit) {
        isLoading = true
        try {
            val response = RetrofitInstance.tmdbApi.getTrending(BuildConfig.TMDB_API_KEY)
            results = response.results
        } catch (e: Exception) {
            errorMessage = e.message
        } finally {
            isLoading = false
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(text = "Discover", style = MaterialTheme.typography.headlineMedium)

        OutlinedTextField(
            value = query,
            onValueChange = { newQuery ->
                query = newQuery
                scope.launch {
                    isLoading = true
                    errorMessage = null
                    try {
                        results = if (newQuery.isBlank()) {
                            RetrofitInstance.tmdbApi.getTrending(BuildConfig.TMDB_API_KEY).results
                        } else {
                            RetrofitInstance.tmdbApi.searchMulti(newQuery, BuildConfig.TMDB_API_KEY).results
                        }
                    } catch (e: Exception) {
                        errorMessage = e.message
                    } finally {
                        isLoading = false
                    }
                }
            },
            label = { Text("Search movies & TV shows") },
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
        )

        when {
            isLoading -> CircularProgressIndicator(modifier = Modifier.padding(top = 16.dp))
            errorMessage != null -> Text(text = "Error: $errorMessage")
            else -> LazyColumn {
                items(results) { media ->
                    MediaRow(
                        media = media,
                        onAddToWatchlist = {
                            scope.launch(kotlinx.coroutines.Dispatchers.IO) {
                                db.watchlistDao().insert(
                                    WatchlistItem(
                                        mediaId = media.id,
                                        title = media.displayTitle(),
                                        mediaType = media.media_type ?: "movie",
                                        posterPath = media.poster_path,
                                        overview = media.overview
                                    )
                                )
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun MediaRow(media: TmdbMedia, onAddToWatchlist: () -> Unit) {
    var added by remember { mutableStateOf(false) }

    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
        AsyncImage(
            model = "https://image.tmdb.org/t/p/w200${media.poster_path}",
            contentDescription = media.displayTitle(),
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(width = 80.dp, height = 120.dp)
                .clip(RoundedCornerShape(8.dp))
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f, fill = false)) {
            Text(text = media.displayTitle(), style = MaterialTheme.typography.titleMedium)
            Text(
                text = media.overview?.take(100) ?: "No description available",
                style = MaterialTheme.typography.bodySmall
            )
        }
        IconButton(onClick = {
            onAddToWatchlist()
            added = true
        }) {
            Icon(
                imageVector = if (added) Icons.Default.Check else Icons.Default.Add,
                contentDescription = "Add to watchlist"
            )
        }
    }
}