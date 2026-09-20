package com.cinepulse.app.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "watchlist_items")
data class WatchlistItem(
    @PrimaryKey val mediaId: Int,
    val title: String,
    val mediaType: String, // "movie" or "tv"
    val posterPath: String?,
    val overview: String?,
    val watchStatus: String = "PLAN_TO_WATCH", // PLAN_TO_WATCH, WATCHING, COMPLETED, DROPPED
    val addedAt: Long = System.currentTimeMillis()
)