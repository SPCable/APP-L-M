package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Restaurant
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.AppDatabase
import com.example.data.RestaurantRepository
import com.example.ui.AppTab
import com.example.ui.RestaurantViewModel
import com.example.ui.components.FilterDialog
import com.example.ui.screens.FavoritesScreen
import com.example.ui.screens.ListScreen
import com.example.ui.screens.SwipeScreen
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val context = LocalContext.current
            
            // Core database, DAO and repository hooks
            val database = remember { AppDatabase.getDatabase(context) }
            val repository = remember { RestaurantRepository(database.restaurantDao()) }
            
            // Jetpack ViewModel initialization via custom Factory
            val viewModelFactory = remember { RestaurantViewModel.Factory(repository) }
            val viewModel: RestaurantViewModel = androidx.lifecycle.viewmodel.compose.viewModel(
                factory = viewModelFactory
            )

            MyApplicationTheme {
                MainAppLayout(viewModel = viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppLayout(viewModel: RestaurantViewModel) {
    // Collect states reactively
    val currentTab by viewModel.currentTab.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val filterRating by viewModel.filterRating.collectAsStateWithLifecycle()
    val filterPrices by viewModel.filterPriceRanges.collectAsStateWithLifecycle()
    val filterDistance by viewModel.filterMaxDistance.collectAsStateWithLifecycle()
    val favorites by viewModel.favorites.collectAsStateWithLifecycle()
    val filteredFavorites by viewModel.filteredFavorites.collectAsStateWithLifecycle()
    val swipeDeckRestaurants by viewModel.swipeDeckRestaurants.collectAsStateWithLifecycle()
    val filteredRestaurants by viewModel.filteredRestaurants.collectAsStateWithLifecycle()

    var isFilterDialogOpen by remember { mutableStateOf(false) }

    // Map favorites by ID for instant O(1) checks
    val favoriteMap = remember(favorites) {
        favorites.associateBy { it.id }
    }

    // Compute active filters count
    val activeFiltersCount = remember(filterRating, filterPrices, filterDistance) {
        var count = 0
        if (filterRating > 0.0) count++
        if (filterPrices.isNotEmpty()) count++
        if (filterDistance < 10.0) count++ // Assuming 10.0 is the upper limit/Any boundary
        count
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Restaurant Swiper",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        val subtitle = when (currentTab) {
                            AppTab.SWIPE -> "Swipe right to Save"
                            AppTab.EXPLORE -> "Search & Filter Spots"
                            AppTab.FAVORITES -> "My Dining Scrapbook"
                        }
                        Text(
                            text = subtitle,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                ),
                modifier = Modifier.testTag("app_bar")
            )
        },
        bottomBar = {
            NavigationBar(
                modifier = Modifier
                    .navigationBarsPadding() // Protect against double system bars / gesture pills overlap
                    .fillMaxWidth()
                    .testTag("bottom_nav_bar")
            ) {
                // Tab 1: Swipe Swiping Deck
                NavigationBarItem(
                    selected = currentTab == AppTab.SWIPE,
                    onClick = { viewModel.setTab(AppTab.SWIPE) },
                    icon = {
                        Icon(
                            imageVector = if (currentTab == AppTab.SWIPE) Icons.Filled.Restaurant else Icons.Outlined.Restaurant,
                            contentDescription = "Swipe"
                        )
                    },
                    label = { Text("Swipe") },
                    modifier = Modifier.testTag("nav_tab_swipe")
                )

                // Tab 2: Directory Explore Grid
                NavigationBarItem(
                    selected = currentTab == AppTab.EXPLORE,
                    onClick = { viewModel.setTab(AppTab.EXPLORE) },
                    icon = {
                        Icon(
                            imageVector = if (currentTab == AppTab.EXPLORE) Icons.Filled.Explore else Icons.Outlined.Explore,
                            contentDescription = "Explore"
                        )
                    },
                    label = { Text("Explore") },
                    modifier = Modifier.testTag("nav_tab_explore")
                )

                // Tab 3: Favorites Scrapbook list
                NavigationBarItem(
                    selected = currentTab == AppTab.FAVORITES,
                    onClick = { viewModel.setTab(AppTab.FAVORITES) },
                    icon = {
                        Icon(
                            imageVector = if (currentTab == AppTab.FAVORITES) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                            contentDescription = "Scrapbook"
                        )
                    },
                    label = { Text("Scrapbook") },
                    modifier = Modifier.testTag("nav_tab_favorites")
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            when (currentTab) {
                AppTab.SWIPE -> {
                    // Double list wrapping ensures smooth, reactive key recompositions
                    val swipeWrapper = remember(swipeDeckRestaurants) { listOf(swipeDeckRestaurants) }
                    
                    SwipeScreen(
                        swipeDeck = swipeWrapper,
                        deckRestaurants = swipeDeckRestaurants,
                        onSwipeLeft = { rest -> viewModel.swipeLeft(rest) },
                        onSwipeRight = { rest -> viewModel.swipeRight(rest) },
                        onResetDeck = { viewModel.resetDeck() },
                        favoriteMap = favoriteMap,
                        onSavePersonalReview = { id, notes, rating ->
                            viewModel.updatePersonalNotesAndRating(id, notes, rating)
                        }
                    )
                }

                AppTab.EXPLORE -> {
                    ListScreen(
                        restaurants = filteredRestaurants,
                        query = searchQuery,
                        onQueryChange = { q -> viewModel.updateSearchQuery(q) },
                        activeFavorites = favoriteMap,
                        onToggleFavorite = { rest, isFav ->
                            if (isFav) {
                                viewModel.removeFromFavorites(rest.id)
                            } else {
                                // Add to favorites and save a SwipeAction log
                                viewModel.swipeRight(rest)
                            }
                        },
                        onOpenFilters = { isFilterDialogOpen = true },
                        activeFiltersCount = activeFiltersCount,
                        onSavePersonalReview = { id, notes, rating ->
                            viewModel.updatePersonalNotesAndRating(id, notes, rating)
                        }
                    )
                }

                AppTab.FAVORITES -> {
                    FavoritesScreen(
                        favorites = filteredFavorites,
                        allRestaurants = viewModel.repository.getLocalRestaurants(),
                        onRemoveFavorite = { id -> viewModel.removeFromFavorites(id) },
                        onSavePersonalReview = { id, notes, rating ->
                            viewModel.updatePersonalNotesAndRating(id, notes, rating)
                        },
                        onNavigateToSwipe = { viewModel.setTab(AppTab.SWIPE) }
                    )
                }
            }

            // Filter Configuration modal dialog
            if (isFilterDialogOpen) {
                FilterDialog(
                    currentRating = filterRating,
                    currentPrices = filterPrices,
                    currentMaxDistance = filterDistance,
                    onDismiss = { isFilterDialogOpen = false },
                    onApply = { rating, prices, distance ->
                        viewModel.setRatingFilter(rating)
                        viewModel.setPriceFilters(prices)
                        viewModel.setMaxDistanceFilter(distance)
                        isFilterDialogOpen = false
                    }
                )
            }
        }
    }
}
