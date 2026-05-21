package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface RestaurantDao {
    @Query("SELECT * FROM favorite_restaurants ORDER BY likedAt DESC")
    fun getAllFavorites(): Flow<List<FavoriteRestaurant>>

    @Query("SELECT * FROM favorite_restaurants WHERE id = :id LIMIT 1")
    suspend fun getFavoriteById(id: String): FavoriteRestaurant?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(restaurant: FavoriteRestaurant)

    @Update
    suspend fun updateFavorite(restaurant: FavoriteRestaurant)

    @Delete
    suspend fun deleteFavorite(restaurant: FavoriteRestaurant)

    @Query("DELETE FROM favorite_restaurants WHERE id = :id")
    suspend fun deleteFavoriteById(id: String)

    @Query("SELECT * FROM swiped_actions")
    fun getAllSwipedActions(): Flow<List<SwipedAction>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSwipedAction(action: SwipedAction)

    @Query("DELETE FROM swiped_actions")
    suspend fun clearAllSwipedActions()

    @Query("DELETE FROM favorite_restaurants")
    suspend fun clearAllFavorites()
}
