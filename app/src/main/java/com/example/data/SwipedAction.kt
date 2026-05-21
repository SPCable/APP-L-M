package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "swiped_actions")
data class SwipedAction(
    @PrimaryKey val restaurantId: String,
    val isLiked: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)
