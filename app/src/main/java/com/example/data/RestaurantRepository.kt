package com.example.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RestaurantRepository(
    private val restaurantDao: RestaurantDao,
    private val placesApiService: PlacesApiService = PlacesApiService.create()
) {
    var lastApiStatus: String? = null
        private set
    var lastApiErrorMessage: String? = null
        private set

    // Reactive streams for favorites and swiped actions
    val favorites: Flow<List<FavoriteRestaurant>> = restaurantDao.getAllFavorites()
    val swipedActions: Flow<List<SwipedAction>> = restaurantDao.getAllSwipedActions()

    // Get a live set of swiped IDs for easy O(1) filtering
    val swipedIds: Flow<Set<String>> = swipedActions.map { list ->
        list.map { it.restaurantId }.toSet()
    }

    // Static source list
    fun getLocalRestaurants(): List<Restaurant> = RestaurantData.localRestaurants

    // Haversine formula to compute distance in miles between user and place
    private fun calculateDistanceInMiles(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val theta = lon1 - lon2
        var dist = Math.sin(Math.toRadians(lat1)) * Math.sin(Math.toRadians(lat2)) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.cos(Math.toRadians(theta))
        dist = Math.acos(dist)
        dist = Math.toDegrees(dist)
        dist = dist * 60 * 1.1515
        return Math.round(dist * 10.0) / 10.0
    }

    suspend fun getNearbyRestaurants(
        userLat: Double?,
        userLng: Double?,
        radiusInMeters: Int,
        apiKey: String
    ): List<Restaurant> {
        if (apiKey.isBlank() || apiKey == "PLACEHOLDER" || userLat == null || userLng == null) {
            lastApiStatus = "PLACEHOLDER"
            lastApiErrorMessage = "API Key is missing or placeholder"
            android.util.Log.d("BiteSwipe", "getNearbyRestaurants: Blank/placeholder API key or null location. lat=$userLat, lng=$userLng")
            return RestaurantData.localRestaurants
        }

        return try {
            android.util.Log.d("BiteSwipe", "getNearbyRestaurants: Fetching from Places API. location=$userLat,$userLng, radius=$radiusInMeters")
            val response = placesApiService.getNearbySearch(
                location = "$userLat,$userLng",
                radiusInMeters = radiusInMeters,
                apiKey = apiKey
            )
            
            lastApiStatus = response.status
            lastApiErrorMessage = response.errorMessage
            
            android.util.Log.d("BiteSwipe", "getNearbyRestaurants: Status=${response.status}, ErrorMessage=${response.errorMessage ?: "None"}")
            
            if (response.status == "OK" && response.results.isNotEmpty()) {
                response.results.map { place ->
                    val photoUrl = if (!place.photos.isNullOrEmpty()) {
                        "https://maps.googleapis.com/maps/api/place/photo?maxwidth=800&photo_reference=${place.photos[0].photoReference}&key=$apiKey"
                    } else {
                        // High-quality food fallback images based on type
                        val isSushi = place.types?.any { it.contains("sushi", ignoreCase = true) } ?: false
                        val isPizza = place.types?.any { it.contains("pizza", ignoreCase = true) } ?: false
                        val isMexican = place.types?.any { it.contains("mexican", ignoreCase = true) } ?: false
                        when {
                            isSushi -> "https://images.unsplash.com/photo-1579871494447-9811cf80d66c?q=80&w=600&auto=format&fit=crop"
                            isPizza -> "https://images.unsplash.com/photo-1513104890138-7c749659a591?q=80&w=600&auto=format&fit=crop"
                            isMexican -> "https://images.unsplash.com/photo-1565299585323-38d6b0865b47?q=80&w=600&auto=format&fit=crop"
                            else -> "https://images.unsplash.com/photo-1517248135467-4c7edcad34c4?q=80&w=600&auto=format&fit=crop"
                        }
                    }

                    val priceRangeString = when (place.priceLevel) {
                        0, 1 -> "$"
                        2 -> "$$"
                        3 -> "$$$"
                        4 -> "$$$$"
                        else -> "$$"
                    }

                    val cuisineType = place.types
                        ?.firstOrNull { it != "restaurant" && it != "food" && it != "point_of_interest" && it != "establishment" }
                        ?.replace("_", " ")
                        ?.split(" ")
                        ?.joinToString(" ") { word -> word.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() } }
                        ?: "Restaurant"

                    val computedDistance = calculateDistanceInMiles(userLat, userLng, place.geometry?.location?.lat ?: userLat, place.geometry?.location?.lng ?: userLng)

                    // Generate a few realistic mock reviews for Google Places results
                    val placeReviews = listOf(
                        Review("Google Local Guide", place.rating ?: 4.5, "Authentic experience and wonderful atmosphere. Recommended!", "Recent"),
                        Review("Food Lover", 4.0, "Great location and quick service. Will visit again.", "Recent")
                    )

                    Restaurant(
                        id = place.placeId,
                        name = place.name,
                        imageUrl = photoUrl,
                        cuisine = cuisineType,
                        rating = place.rating ?: 4.0,
                        reviewsCount = place.userRatingsTotal ?: 15,
                        priceRange = priceRangeString,
                        distance = computedDistance,
                        address = place.vicinity ?: "Nearby",
                        description = "A popular place in the area serving delicious $cuisineType.",
                        reviews = placeReviews
                    )
                }
            } else {
                android.util.Log.d("BiteSwipe", "getNearbyRestaurants: Response results are empty or status not OK")
                RestaurantData.localRestaurants
            }
        } catch (e: Exception) {
            lastApiStatus = "EXCEPTION"
            lastApiErrorMessage = e.message
            android.util.Log.e("BiteSwipe", "getNearbyRestaurants: Exception while fetching: ${e.message}", e)
            RestaurantData.localRestaurants
        }
    }

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
