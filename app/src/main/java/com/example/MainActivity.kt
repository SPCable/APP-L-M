package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.border
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.FormatListBulleted
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.LocalShipping
import androidx.compose.material.icons.outlined.Restaurant
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.AppDatabase
import com.example.data.LocationService
import com.example.data.RestaurantRepository
import com.example.ui.AppTab
import com.example.ui.RestaurantViewModel
import com.example.ui.components.FilterDialog
import com.example.ui.screens.FavoritesScreen
import com.example.ui.screens.ListScreen
import com.example.ui.screens.SwipeScreen
import com.example.ui.theme.MyApplicationTheme
import kotlinx.coroutines.launch
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.Spring
import com.example.ui.AuthViewModel
import com.example.ui.screens.LoginScreen
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import android.util.Log

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

            // Auth ViewModel for session management
            val authViewModel: AuthViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
            authViewModel.initialize(context)

            val currentUser by authViewModel.currentUser.collectAsStateWithLifecycle()
            val isDemoUser by authViewModel.isDemoUser.collectAsStateWithLifecycle()
            val isAuthLoading by authViewModel.isLoading.collectAsStateWithLifecycle()
            val authErrorMessage by authViewModel.errorMessage.collectAsStateWithLifecycle()

            // Initialize Google Sign-In safely to support systems with or without real google-services.json
            val clientIdResId = context.resources.getIdentifier("default_web_client_id", "string", context.packageName)
            val webClientId = if (clientIdResId != 0) context.getString(clientIdResId) else "602287814407-dummyclientid12345.apps.googleusercontent.com"
            val gso = remember(webClientId) {
                GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(webClientId)
                    .requestEmail()
                    .build()
            }
            val googleSignInClient = remember(context, gso) {
                GoogleSignIn.getClient(context, gso)
            }

            val googleSignInLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.StartActivityForResult()
            ) { result ->
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                try {
                    val account = task.getResult(ApiException::class.java)
                    val idToken = account.idToken
                    if (idToken != null) {
                        authViewModel.signInWithGoogle(idToken) {
                            Toast.makeText(context, "Chào mừng đăng nhập!", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        authViewModel.enterDemoMode {
                            Toast.makeText(context, "Đăng nhập nhanh Demo!", Toast.LENGTH_SHORT).show()
                        }
                    }
                } catch (e: Exception) {
                    Log.e("MainActivity", "Google sign in failed, entering Demo: ${e.message}")
                    authViewModel.enterDemoMode {
                        Toast.makeText(context, "Đăng nhập nhanh Demo!", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            MyApplicationTheme {
                if (currentUser == null && !isDemoUser) {
                    LoginScreen(
                        isLoading = isAuthLoading,
                        errorMessage = authErrorMessage,
                        onGoogleSignInClick = {
                            googleSignInLauncher.launch(googleSignInClient.signInIntent)
                        },
                        onDemoSignInClick = {
                            authViewModel.enterDemoMode {
                                Toast.makeText(context, "Chào mừng bạn đến với LỤM!", Toast.LENGTH_SHORT).show()
                            }
                        }
                    )
                } else {
                    MainAppLayout(viewModel = viewModel, authViewModel = authViewModel)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppLayout(viewModel: RestaurantViewModel, authViewModel: AuthViewModel) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // Collect user session information
    val currentUser by authViewModel.currentUser.collectAsStateWithLifecycle()
    val isDemoUser by authViewModel.isDemoUser.collectAsStateWithLifecycle()
    val demoUserEmail by authViewModel.demoUserEmail.collectAsStateWithLifecycle()
    val demoUserName by authViewModel.demoUserName.collectAsStateWithLifecycle()

    val userName = currentUser?.displayName ?: demoUserName ?: "LỤM User"
    val userEmail = currentUser?.email ?: demoUserEmail ?: "user@lum.vn"
    val userPhotoUrl = currentUser?.photoUrl?.toString()

    var isProfileMenuExpanded by remember { mutableStateOf(false) }

    // Core Location Service and API Key Hooks
    val locationService = remember { LocationService(context) }
    // Fetch Google Places API Key from BuildConfig (injected via secrets plugin from .env file)
    val apiKey = remember { BuildConfig.PLACES_API_KEY ?: "" }
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
    val isLoadingNearby by viewModel.isLoadingNearby.collectAsStateWithLifecycle()
    val dataStatus by viewModel.dataStatus.collectAsStateWithLifecycle()
    val apiStatus by viewModel.apiStatus.collectAsStateWithLifecycle()
    val apiErrorMessage by viewModel.apiErrorMessage.collectAsStateWithLifecycle()
    val forceDemo by viewModel.forceDemo.collectAsStateWithLifecycle()
    val locationLabel by viewModel.locationLabel.collectAsStateWithLifecycle()

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
        if (filterDistance < 10.0) count++
        count
    }

    // Permission launcher to handle dynamic Location prompt
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions[android.Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[android.Manifest.permission.ACCESS_COARSE_LOCATION] == true
        coroutineScope.launch {
            if (granted) {
                val loc = locationService.getCurrentLocation()
                viewModel.fetchNearbyRestaurants(
                    userLat = loc?.latitude ?: 21.0285,
                    userLng = loc?.longitude ?: 105.8542,
                    radiusInMeters = 8000,
                    apiKey = apiKey
                )
            } else {
                // If denied, fallback gracefully to mock local data using blank API Key
                viewModel.fetchNearbyRestaurants(
                    userLat = 21.0285,
                    userLng = 105.8542,
                    radiusInMeters = 8000,
                    apiKey = ""
                )
            }
        }
    }

    // Scan for location on start or when forceDemo changes
    LaunchedEffect(forceDemo) {
        val hasFine = androidx.core.content.ContextCompat.checkSelfPermission(
            context,
            android.Manifest.permission.ACCESS_FINE_LOCATION
        ) == android.content.pm.PackageManager.PERMISSION_GRANTED
        val hasCoarse = androidx.core.content.ContextCompat.checkSelfPermission(
            context,
            android.Manifest.permission.ACCESS_COARSE_LOCATION
        ) == android.content.pm.PackageManager.PERMISSION_GRANTED

        if (hasFine || hasCoarse) {
            val loc = locationService.getCurrentLocation()
            viewModel.fetchNearbyRestaurants(
                userLat = loc?.latitude ?: 21.0285,
                userLng = loc?.longitude ?: 105.8542,
                radiusInMeters = 8000,
                apiKey = apiKey
            )
        } else {
            locationPermissionLauncher.launch(
                arrayOf(
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            Column {
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
                    actions = {
                        Box(modifier = Modifier.padding(end = 16.dp)) {
                            if (userPhotoUrl != null) {
                                coil.compose.AsyncImage(
                                    model = userPhotoUrl,
                                    contentDescription = "Profile Picture",
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .border(if (isDemoUser) BorderStroke(1.5.dp, Color(0xFF4CAF50)) else BorderStroke(0.dp, Color.Transparent), CircleShape)
                                        .clickable { isProfileMenuExpanded = true }
                                )
                            } else {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .background(MaterialTheme.colorScheme.primaryContainer, CircleShape)
                                        .border(BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)), CircleShape)
                                        .clickable { isProfileMenuExpanded = true },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = userName.take(1).uppercase(),
                                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp
                                    )
                                }
                            }

                            DropdownMenu(
                                expanded = isProfileMenuExpanded,
                                onDismissRequest = { isProfileMenuExpanded = false }
                            ) {
                                DropdownMenuItem(
                                    text = {
                                        Column {
                                            Text(text = userName, fontWeight = FontWeight.Bold)
                                            Text(
                                                text = userEmail,
                                                fontSize = 11.sp,
                                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                            )
                                        }
                                    },
                                    onClick = {},
                                    enabled = false
                                )
                                HorizontalDivider()
                                DropdownMenuItem(
                                    text = { Text("Đăng xuất", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold) },
                                    onClick = {
                                        isProfileMenuExpanded = false
                                        authViewModel.signOut {
                                            Toast.makeText(context, "Đã đăng xuất!", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                )
                            }
                        }
                    },
                    modifier = Modifier.testTag("app_bar")
                )
                StatusAndLocationBanner(
                    dataStatus = dataStatus,
                    apiStatus = apiStatus,
                    apiErrorMessage = apiErrorMessage,
                    locationLabel = locationLabel,
                    forceDemo = forceDemo,
                    onToggleForceDemo = { viewModel.toggleForceDemo() }
                )
            }
        },
        bottomBar = { BottomBar(viewModel = viewModel, currentTab = currentTab) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            when (currentTab) {
                AppTab.SWIPE -> {
                    val swipeWrapper =
                        remember(swipeDeckRestaurants) { listOf(swipeDeckRestaurants) }

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
                        allRestaurants = viewModel.nearbyRestaurants.value,
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

            // High-end Dark Theme Scanning overlay when fetching Places API
            if (isLoadingNearby) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background.copy(alpha = 0.9f)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.primary,
                            strokeWidth = 4.dp,
                            modifier = Modifier.size(56.dp)
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        Text(
                            text = "SCANNING NEARBY SPOTS...",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.5.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StatusAndLocationBanner(
    dataStatus: String,
    apiStatus: String?,
    apiErrorMessage: String?,
    locationLabel: String,
    forceDemo: Boolean,
    onToggleForceDemo: () -> Unit
) {
    val isBillingError = apiStatus == "REQUEST_DENIED" && apiErrorMessage?.contains(
        "billing",
        ignoreCase = true
    ) == true

    val backgroundColor = when {
        dataStatus == "ONLINE" -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f)
        isBillingError -> MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.2f)
        dataStatus == "OFFLINE_FALLBACK" -> MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.2f)
        else -> MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.2f)
    }

    val textColor = when {
        dataStatus == "ONLINE" -> MaterialTheme.colorScheme.primary
        isBillingError -> MaterialTheme.colorScheme.error
        dataStatus == "OFFLINE_FALLBACK" -> MaterialTheme.colorScheme.secondary
        else -> MaterialTheme.colorScheme.error
    }

    val statusText = when {
        dataStatus == "ONLINE" -> "Real-time Places Active"
        isBillingError -> "Google Cloud Billing Disabled"
        dataStatus == "OFFLINE_FALLBACK" -> "Demo Mode (Offline)"
        else -> "Demo Fallback (API Error)"
    }

    val iconText = when {
        dataStatus == "ONLINE" -> "🟢"
        isBillingError -> "🔴"
        dataStatus == "OFFLINE_FALLBACK" -> "🟡"
        else -> "🟠"
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(backgroundColor)
            .padding(horizontal = 16.dp, vertical = 6.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = iconText, fontSize = 10.sp)
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = statusText,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = textColor
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Sleek pill-shaped Toggle Button
                TextButton(
                    onClick = onToggleForceDemo,
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                    modifier = Modifier
                        .height(24.dp)
                        .background(
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                            shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
                        )
                ) {
                    Text(
                        text = if (forceDemo) "Use Real GPS" else "Switch to Demo",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = locationLabel,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                    textAlign = TextAlign.End
                )
            }
        }

        // Show detailed user instructions if billing is disabled or key has errors
        if (isBillingError) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Google Cloud Places API requires billing enabled. Please enable billing on your Google Cloud project to fetch real restaurants near your location.",
                fontSize = 10.sp,
                lineHeight = 13.sp,
                fontWeight = FontWeight.Normal,
                color = MaterialTheme.colorScheme.error.copy(alpha = 0.8f)
            )
        } else if (dataStatus == "OFFLINE_ERROR" && apiStatus != null) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Places API Error: $apiStatus${if (apiErrorMessage != null) " - $apiErrorMessage" else ""}",
                fontSize = 10.sp,
                lineHeight = 13.sp,
                fontWeight = FontWeight.Normal,
                color = MaterialTheme.colorScheme.error.copy(alpha = 0.8f)
            )
        }
    }
}
@Composable
fun BottomBar(viewModel: RestaurantViewModel, currentTab: AppTab) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(bottom = 12.dp, start = 24.dp, end = 24.dp),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            modifier = Modifier
                .height(68.dp)
                .fillMaxWidth()
                .shadow(
                    elevation = 16.dp,
                    shape = RoundedCornerShape(36.dp),
                    clip = false
                ),
            shape = RoundedCornerShape(36.dp),
            color = MaterialTheme.colorScheme.surface
        ) {
            BoxWithConstraints(
                modifier = Modifier.fillMaxSize()
            ) {
                val tabWidth = maxWidth / 3
                val targetOffset = when (currentTab) {
                    AppTab.SWIPE -> 0.dp
                    AppTab.EXPLORE -> tabWidth
                    AppTab.FAVORITES -> tabWidth * 2
                }

                val animatedOffset by animateDpAsState(
                    targetValue = targetOffset,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioLowBouncy,
                        stiffness = Spring.StiffnessMediumLow
                    ),
                    label = "indicatorOffset"
                )

                // 1. Sliding Green Circle Indicator
                Box(
                    modifier = Modifier
                        .offset(x = animatedOffset)
                        .width(tabWidth)
                        .fillMaxHeight(),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .background(Color(0xFFE2F9E5), CircleShape)
                    )
                }

                // 2. Interactive Tab Row on top
                Row(
                    modifier = Modifier.fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Tab 1: Home/Swipe
                    val isSwipe = currentTab == AppTab.SWIPE
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                                onClick = { viewModel.setTab(AppTab.SWIPE) }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Home,
                            contentDescription = "Swipe",
                            tint = if (isSwipe) Color(0xFF0F3A20) else Color(0xFF5E6D63),
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    // Tab 2: Directory/Explore
                    val isExplore = currentTab == AppTab.EXPLORE
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                                onClick = { viewModel.setTab(AppTab.EXPLORE) }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.FormatListBulleted,
                            contentDescription = "Explore",
                            tint = if (isExplore) Color(0xFF0F3A20) else Color(0xFF5E6D63),
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    // Tab 3: Scrapbook/Favorites
                    val isFavorites = currentTab == AppTab.FAVORITES
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                                onClick = { viewModel.setTab(AppTab.FAVORITES) }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.FavoriteBorder,
                            contentDescription = "Scrapbook",
                            tint = if (isFavorites) Color(0xFF0F3A20) else Color(0xFF5E6D63),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun BottomBarPreview() {
    val viewModel = RestaurantViewModel(RestaurantRepository(AppDatabase.getDatabase(LocalContext.current).restaurantDao()))
    val currentTab = AppTab.SWIPE
    BottomBar(viewModel = viewModel, currentTab = currentTab)
}
