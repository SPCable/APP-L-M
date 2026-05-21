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

    // All local restaurants matching current search & filters
    val filteredRestaurants: StateFlow<List<Restaurant>> = combine(
        flowOf(repository.getLocalRestaurants()),
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
