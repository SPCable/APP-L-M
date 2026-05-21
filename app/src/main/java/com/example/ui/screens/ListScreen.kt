package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.FilterList
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Restaurant
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.FavoriteRestaurant
import com.example.data.Restaurant
import com.example.ui.components.DetailDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListScreen(
    restaurants: List<Restaurant>,
    query: String,
    onQueryChange: (String) -> Unit,
    activeFavorites: Map<String, FavoriteRestaurant>,
    onToggleFavorite: (Restaurant, Boolean) -> Unit,
    onOpenFilters: () -> Unit,
    activeFiltersCount: Int,
    onSavePersonalReview: (id: String, notes: String, rating: Double?) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedRestaurantForDetail by remember { mutableStateOf<Restaurant?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Double Decker Search Bar + Filter Button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Outlined elegant search field - matching bg-[#1E1F22], rounded-full, border-[#2D2E32]
            OutlinedTextField(
                value = query,
                onValueChange = onQueryChange,
                placeholder = { Text("Search restaurants, cuisines...", color = MaterialTheme.colorScheme.secondary) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = MaterialTheme.colorScheme.secondary
                    )
                },
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp)
                    .testTag("explore_search_input"),
                shape = CircleShape,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    focusedBorderColor = MaterialTheme.colorScheme.outline,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                ),
                singleLine = true
            )

            // Circular Filter Trigger with badge for active filters count
            BadgedBox(
                badge = {
                    if (activeFiltersCount > 0) {
                        Badge(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.testTag("filter_badge")
                        ) {
                            Text("$activeFiltersCount", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            ) {
                IconButton(
                    onClick = onOpenFilters,
                    modifier = Modifier
                        .size(52.dp)
                        .background(MaterialTheme.colorScheme.surface, CircleShape)
                        .border(1.dp, MaterialTheme.colorScheme.outline, CircleShape)
                        .testTag("filter_button")
                ) {
                    Icon(
                        imageVector = Icons.Outlined.FilterList,
                        contentDescription = "Filter Options",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }

        // Horizontal status header
        Text(
            text = "MATCHING LOCAL PLACES (${restaurants.size})",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // List display
        if (restaurants.isEmpty()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Restaurant,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "No Restorants Found",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "We couldn't find items that match your search query or filter specifications. Try adjusting your preferences!",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                        textAlign = TextAlign.Center,
                        lineHeight = 20.sp
                    )
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier
                    .weight(1f)
                    .testTag("explore_restaurant_list")
            ) {
                items(restaurants, key = { it.id }) { restaurant ->
                    val isFavorite = activeFavorites.containsKey(restaurant.id)

                    ExploreCard(
                        restaurant = restaurant,
                        isFavorite = isFavorite,
                        onToggleFavorite = { onToggleFavorite(restaurant, isFavorite) },
                        onCardClick = { selectedRestaurantForDetail = restaurant }
                    )
                }
            }
        }

        // Detailed review sheet dialog
        selectedRestaurantForDetail?.let { r ->
            DetailDialog(
                restaurant = r,
                favoriteInfo = activeFavorites[r.id],
                onDismiss = { selectedRestaurantForDetail = null },
                onSavePersonalData = { notes, rating ->
                    onSavePersonalReview(r.id, notes, rating)
                }
            )
        }
    }
}

@Composable
fun ExploreCard(
    restaurant: Restaurant,
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    onCardClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onCardClick)
            .testTag("restaurant_explore_card_${restaurant.id}"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Rounded thumbnail
            AsyncImage(
                model = restaurant.imageUrl,
                contentDescription = restaurant.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(90.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color.LightGray)
            )

            Spacer(modifier = Modifier.width(16.dp))

            // Information Center
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = restaurant.cuisine.uppercase(),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    letterSpacing = 0.5.sp
                )

                Text(
                    text = restaurant.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Star row + distance
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(6.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            text = "${restaurant.rating}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }

                    Text(
                        text = "(${restaurant.reviewsCount})",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )

                    Text(
                        text = "•",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Outlined.LocationOn,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(1.dp))
                        Text(
                            text = "${restaurant.distance} mi",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Price and address tags
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = restaurant.priceRange,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = restaurant.address,
                        fontSize = 11.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                }
            }

            // Save Toggle Button
            IconButton(
                onClick = onToggleFavorite,
                modifier = Modifier.testTag("favorite_toggle_${restaurant.id}")
            ) {
                Icon(
                    imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "Save Favorite",
                    tint = if (isFavorite) Color(0xFFEF5350) else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.35f),
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}
