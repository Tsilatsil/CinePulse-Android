package com.cinepulse.app.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface WatchlistDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(item: WatchlistItem): Long

    @Delete
    fun delete(item: WatchlistItem): Int

    @Query("SELECT * FROM watchlist_items ORDER BY addedAt DESC")
    fun getAll(): Flow<List<WatchlistItem>>

    @Query("SELECT EXISTS(SELECT 1 FROM watchlist_items WHERE mediaId = :mediaId)")
    fun isInWatchlist(mediaId: Int): Boolean
}