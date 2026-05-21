package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.FavoriteRestaurant
import com.example.data.Restaurant
import com.example.data.RestaurantRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

enum class AppTab {
    SWIPE,      // Tinder-style card stacking swiper
    EXPLORE,    // Feed view of all matching restaurants in the area
    FAVORITES   // Saved list of favorite spots with personal diaries / rating
}

class RestaurantViewModel(val repository: RestaurantRepository) : ViewModel() {

    // Tab state
    private val _currentTab = MutableStateFlow(AppTab.SWIPE)
    val currentTab: StateFlow<AppTab> = _currentTab.asStateFlow()

    // Search and filters state
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _filterRating = MutableStateFlow(0.0) // 0.0 means "Any"
    val filterRating: StateFlow<Double> = _filterRating.asStateFlow()

    // Selected price categories, e.g., setOf("$", "$$", "$$$")
    private val _filterPriceRanges = MutableStateFlow<Set<String>>(emptySet()) // empty means "Any"
    val filterPriceRanges: StateFlow<Set<String>> = _filterPriceRanges.asStateFlow()

    // Max distance in miles
    private val _filterMaxDistance = MutableStateFlow(5.0) // default 5.0 miles max
    val filterMaxDistance: StateFlow<Double> = _filterMaxDistance.asStateFlow()

    // Swiped IDs from database
    val swipedIds: StateFlow<Set<String>> = repository.swipedIds
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptySet()
        )

    // Favorites list from database
    val favorites: StateFlow<List<FavoriteRestaurant>> = repository.favorites
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Filtered favorites (reactive to search query)
    val filteredFavorites: StateFlow<List<FavoriteRestaurant>> = combine(
        favorites,
        _searchQuery
    ) { favList, query ->
        if (query.isBlank()) {
            favList
        } else {
            favList.filter {
                it.name.contains(query, ignoreCase = true) ||
                it.cuisine.contains(query, ignoreCase = true) ||
                it.address.contains(query, ignoreCase = true)
            }
        }
    }
    .stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )
    // Loading state for online Places API scan
    private val _isLoadingNearby = MutableStateFlow(false)
    val isLoadingNearby: StateFlow<Boolean> = _isLoadingNearby.asStateFlow()

    // Status of data fetching (ONLINE, OFFLINE_FALLBACK, OFFLINE_ERROR)
    private val _dataStatus = MutableStateFlow("OFFLINE_FALLBACK")
    val dataStatus: StateFlow<String> = _dataStatus.asStateFlow()

    // Places API specific status and error message
    private val _apiStatus = MutableStateFlow<String?>(null)
    val apiStatus: StateFlow<String?> = _apiStatus.asStateFlow()

    private val _apiErrorMessage = MutableStateFlow<String?>(null)
    val apiErrorMessage: StateFlow<String?> = _apiErrorMessage.asStateFlow()

    // Force Demo Mode state
    private val _forceDemo = MutableStateFlow(false)
    val forceDemo: StateFlow<Boolean> = _forceDemo.asStateFlow()

    // Human-readable active location label
    private val _locationLabel = MutableStateFlow("Default (Vietnam)")
    val locationLabel: StateFlow<String> = _locationLabel.asStateFlow()

    // Dynamic fetched list of restaurants (either local fallback or online Google Places)
    private val _nearbyRestaurants = MutableStateFlow<List<Restaurant>>(repository.getLocalRestaurants())
    val nearbyRestaurants: StateFlow<List<Restaurant>> = _nearbyRestaurants.asStateFlow()

    // All matching restaurants reactively filtered
    val filteredRestaurants: StateFlow<List<Restaurant>> = combine(
        nearbyRestaurants,
        _searchQuery,
        _filterRating,
        _filterPriceRanges,
        _filterMaxDistance
    ) { restaurants, query, rating, prices, maxDistance ->
        restaurants.filter { rest ->
            // 1. Search Query
            val matchesQuery = query.isBlank() ||
                    rest.name.contains(query, ignoreCase = true) ||
                    rest.cuisine.contains(query, ignoreCase = true)
            
            // 2. Rating
            val matchesRating = rest.rating >= rating

            // 3. Price
            val matchesPrice = prices.isEmpty() || prices.contains(rest.priceRange)

            // 4. Distance
            val matchesDistance = rest.distance <= maxDistance

            matchesQuery && matchesRating && matchesPrice && matchesDistance
        }
    }
    .stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // The Tinder deck: matching restaurants that have NOT been swiped yet
    val swipeDeckRestaurants: StateFlow<List<Restaurant>> = combine(
        filteredRestaurants,
        swipedIds
    ) { list, swiped ->
        list.filter { it.id !in swiped }
    }
    .stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Action to fetch online Google Places data
    fun fetchNearbyRestaurants(userLat: Double?, userLng: Double?, radiusInMeters: Int, apiKey: String) {
        viewModelScope.launch {
            _isLoadingNearby.value = true
            
            // Setup location label
            if (userLat != null && userLng != null) {
                if (userLat == 21.0285 && userLng == 105.8542) {
                    _locationLabel.value = "Default (Vietnam)"
                } else {
                    _locationLabel.value = String.format("GPS: %.4f, %.4f", userLat, userLng)
                }
            } else {
                _locationLabel.value = "No GPS (Default Location)"
            }

            android.util.Log.d("BiteSwipe", "fetchNearbyRestaurants called for location: ${_locationLabel.value}, forceDemo=${_forceDemo.value}")

            // Dynamic online fetch
            val results = if (_forceDemo.value) {
                repository.getLocalRestaurants()
            } else {
                repository.getNearbyRestaurants(userLat, userLng, radiusInMeters, apiKey)
            }
            _nearbyRestaurants.value = results
            
            _apiStatus.value = if (_forceDemo.value) null else repository.lastApiStatus
            _apiErrorMessage.value = if (_forceDemo.value) null else repository.lastApiErrorMessage

            // Setup status
            if (_forceDemo.value || apiKey.isBlank() || apiKey == "PLACEHOLDER") {
                _dataStatus.value = "OFFLINE_FALLBACK"
            } else if (results == com.example.data.RestaurantData.localRestaurants) {
                _dataStatus.value = "OFFLINE_ERROR"
            } else {
                _dataStatus.value = "ONLINE"
            }

            android.util.Log.d("BiteSwipe", "fetchNearbyRestaurants completed. Loaded: ${results.size} restaurants, Status: ${_dataStatus.value}")
            _isLoadingNearby.value = false
        }
    }

    fun toggleForceDemo() {
        _forceDemo.value = !_forceDemo.value
    }

    // Actions

    fun setTab(tab: AppTab) {
        _currentTab.value = tab
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setRatingFilter(rating: Double) {
        _filterRating.value = rating
    }

    fun togglePriceFilter(price: String) {
        val current = _filterPriceRanges.value
        _filterPriceRanges.value = if (current.contains(price)) {
            current - price
        } else {
            current + price
        }
    }

    fun setPriceFilters(prices: Set<String>) {
        _filterPriceRanges.value = prices
    }

    fun setMaxDistanceFilter(distance: Double) {
        _filterMaxDistance.value = distance
    }

    // Swipe operations
    fun swipeLeft(restaurant: Restaurant) {
        viewModelScope.launch {
            repository.saveSwipe(restaurant.id, isLiked = false)
        }
    }

    fun swipeRight(restaurant: Restaurant) {
        viewModelScope.launch {
            repository.saveSwipe(restaurant.id, isLiked = true)
            repository.saveFavorite(restaurant)
        }
    }

    // Favorite actions
    fun removeFromFavorites(id: String) {
        viewModelScope.launch {
            repository.removeFavorite(id)
            // Note: swipe remains logged so it doesn't immediately reappear in swiper,
            // but if desired, we could remove from swiped_actions so it returns to cards deck,
            // or we keep swipes intact. Let's keep swipe logged as swiped, which is cleaner.
        }
    }

    fun updatePersonalNotesAndRating(id: String, notes: String, rating: Double?) {
        viewModelScope.launch {
            val currentFav = favorites.value.firstOrNull { it.id == id }
            if (currentFav != null) {
                val updated = currentFav.copy(
                    personalNotes = notes,
                    personalRating = rating
                )
                repository.updateFavorite(updated)
            }
        }
    }

    // Administrative controls
    fun resetDeck() {
        viewModelScope.launch {
            repository.resetSwipes()
        }
    }

    fun clearAllFavorites() {
        viewModelScope.launch {
            repository.clearFavorites()
        }
    }

    // Factory Class for direct VM instantiation
    class Factory(private val repository: RestaurantRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(RestaurantViewModel::class.java)) {
                return RestaurantViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
