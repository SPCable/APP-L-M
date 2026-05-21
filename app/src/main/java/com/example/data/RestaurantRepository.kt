package com.example.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RestaurantRepository(private val restaurantDao: RestaurantDao) {

    // Reactive streams for favorites and swiped actions
    val favorites: Flow<List<FavoriteRestaurant>> = restaurantDao.getAllFavorites()
    val swipedActions: Flow<List<SwipedAction>> = restaurantDao.getAllSwipedActions()

    // Get a live set of swiped IDs for easy O(1) filtering
    val swipedIds: Flow<Set<String>> = swipedActions.map { list ->
        list.map { it.restaurantId }.toSet()
    }

    // Static source list
    fun getLocalRestaurants(): List<Restaurant> = RestaurantData.localRestaurants

    suspend fun saveFavorite(restaurant: Restaurant) {
        val favorite = FavoriteRestaurant(
            id = restaurant.id,
            name = restaurant.name,
            imageUrl = restaurant.imageUrl,
            cuisine = restaurant.cuisine,
            rating = restaurant.rating,
            reviewsCount = restaurant.reviewsCount,
            priceRange = restaurant.priceRange,
            distance = restaurant.distance,
            address = restaurant.address,
            likedAt = System.currentTimeMillis()
        )
        restaurantDao.insertFavorite(favorite)
    }

    suspend fun removeFavorite(restaurantId: String) {
        restaurantDao.deleteFavoriteById(restaurantId)
    }

    suspend fun updateFavorite(favorite: FavoriteRestaurant) {
        restaurantDao.updateFavorite(favorite)
    }

    suspend fun saveSwipe(restaurantId: String, isLiked: Boolean) {
        restaurantDao.insertSwipedAction(SwipedAction(restaurantId, isLiked))
    }

    suspend fun resetSwipes() {
        restaurantDao.clearAllSwipedActions()
    }

    suspend fun clearFavorites() {
        restaurantDao.clearAllFavorites()
    }
}
