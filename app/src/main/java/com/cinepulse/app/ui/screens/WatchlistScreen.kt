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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.cinepulse.app.data.local.AppDatabase
import com.cinepulse.app.data.local.WatchlistItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun WatchlistScreen() {
    val context = LocalContext.current
    val db = remember { AppDatabase.getInstance(context) }
    val scope = rememberCoroutineScope()

    val items by db.watchlistDao().getAll().collectAsState(initial = emptyList())

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(text = "My Watchlist", style = MaterialTheme.typography.headlineMedium)

        if (items.isEmpty()) {
            Text(
                text = "Nothing here yet. Add titles from the Discover tab.",
                modifier = Modifier.padding(top = 16.dp)
            )
        }

        LazyColumn {
            items(items) { item ->
                WatchlistRow(
                    item = item,
                    onRemove = {
                        scope.launch(Dispatchers.IO) {
                            db.watchlistDao().delete(item)
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun WatchlistRow(item: WatchlistItem, onRemove: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
        AsyncImage(
            model = "https://image.tmdb.org/t/p/w200${item.posterPath}",
            contentDescription = item.title,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(width = 80.dp, height = 120.dp)
                .clip(RoundedCornerShape(8.dp))
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f, fill = false)) {
            Text(text = item.title, style = MaterialTheme.typography.titleMedium)
            Text(text = item.watchStatus, style = MaterialTheme.typography.bodySmall)
        }
        IconButton(onClick = onRemove) {
            Icon(Icons.Default.Delete, contentDescription = "Remove from watchlist")
        }
    }
}