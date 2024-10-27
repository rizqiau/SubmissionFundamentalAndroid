package com.example.submissionone.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface FavoriteEventDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(event: FavoriteEvent)

    @Delete
    suspend fun deleteFavorite(event: FavoriteEvent)

    @Query("SELECT * FROM FavoriteEvent WHERE id = :eventId LIMIT 1")
    suspend fun getFavoriteById(eventId: String): FavoriteEvent?

    @Query("SELECT * FROM FavoriteEvent")
    suspend fun getAllFavorites(): List<FavoriteEvent>
}

