package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_restaurants")
data class FavoriteRestaurant(
    @PrimaryKey val id: String,
    val name: String,
    val imageUrl: String,
    val cuisine: String,
    val rating: Double,
    val reviewsCount: Int,
    val priceRange: String,
    val distance: Double,
    val address: String,
    val likedAt: Long = System.currentTimeMillis(),
    val personalNotes: String = "",
    val personalRating: Double? = null
)
