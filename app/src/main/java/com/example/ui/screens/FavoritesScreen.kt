package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.EditNote
import androidx.compose.material.icons.outlined.Favorite
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

@Composable
fun FavoritesScreen(
    favorites: List<FavoriteRestaurant>,
    allRestaurants: List<Restaurant>,
    onRemoveFavorite: (String) -> Unit,
    onSavePersonalReview: (id: String, notes: String, rating: Double?) -> Unit,
    onNavigateToSwipe: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedRestaurantForDetail by remember { mutableStateOf<Restaurant?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Top informative header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "MY CULINARY SCRAPBOOK",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                letterSpacing = 1.sp
            )
            
            Surface(
                shape = RoundedCornerShape(50.dp),
                color = MaterialTheme.colorScheme.secondaryContainer,
                contentColor = MaterialTheme.colorScheme.onSecondaryContainer
            ) {
                Text(
                    text = "${favorites.size} saved",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                )
            }
        }

        if (favorites.isEmpty()) {
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
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .background(MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.4f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Favorite,
                            contentDescription = null,
                            tint = Color(0xFFEF5350),
                            modifier = Modifier.size(36.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(18.dp))
                    Text(
                        text = "Your Scrapbook is Empty",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Swipe right on restaurant cards, or press hearts on the explore tab to save restaurants here. You can even write custom notes and ratings of your dining memories!",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                        textAlign = TextAlign.Center,
                        lineHeight = 20.sp
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = onNavigateToSwipe,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth(0.8f)
                            .height(48.dp)
                            .testTag("go_swipe_button")
                    ) {
                        Text("Start Swiping Now")
                    }
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier
                    .weight(1f)
                    .testTag("favorites_list")
            ) {
                items(favorites, key = { it.id }) { favorite ->
                    val matchingStaticData = allRestaurants.firstOrNull { it.id == favorite.id }

                    FavoriteListCard(
                        favorite = favorite,
                        onCardClick = {
                            if (matchingStaticData != null) {
                                selectedRestaurantForDetail = matchingStaticData
                            }
                        },
                        onRemove = { onRemoveFavorite(favorite.id) }
                    )
                }
            }
        }

        // Expanded detail sheet modal
        selectedRestaurantForDetail?.let { r ->
            DetailDialog(
                restaurant = r,
                favoriteInfo = favorites.firstOrNull { it.id == r.id },
                onDismiss = { selectedRestaurantForDetail = null },
                onSavePersonalData = { notes, rating ->
                    onSavePersonalReview(r.id, notes, rating)
                }
            )
        }
    }
}

@Composable
fun FavoriteListCard(
    favorite: FavoriteRestaurant,
    onCardClick: () -> Unit,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onCardClick)
            .testTag("favorite_card_${favorite.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Upper Info Section
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalAlignment = Alignment.Top
            ) {
                AsyncImage(
                    model = favorite.imageUrl,
                    contentDescription = favorite.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(64.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color.LightGray)
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = favorite.cuisine,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = favorite.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = favorite.address,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Delete Button
                IconButton(
                    onClick = onRemove,
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.15f), CircleShape)
                        .size(36.dp)
                        .testTag("remove_favorite_button_${favorite.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Remove Favorite",
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            // Lower User's Personal Notes & Diary Card Section if available
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f))
                    .padding(horizontal = 14.dp, vertical = 10.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "My Rating: ",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        if (favorite.personalRating != null) {
                            Row(horizontalArrangement = Arrangement.spacedBy(1.dp)) {
                                for (i in 1..5) {
                                    Icon(
                                        imageVector = if (i <= favorite.personalRating) Icons.Filled.Star else Icons.Filled.Star,
                                        contentDescription = null,
                                        tint = if (i <= favorite.personalRating) Color(0xFFFFD700) else Color.LightGray.copy(alpha = 0.5f),
                                        modifier = Modifier.size(13.dp)
                                    )
                                }
                            }
                        } else {
                            Text(
                                text = "Unspecified",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                            )
                        }

                        Spacer(modifier = Modifier.weight(1f))

                        // Trigger Detail click which carries Diary edit controls
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable(onClick = onCardClick)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.EditNote,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(
                                text = "Edit Diary",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = if (favorite.personalNotes.isNotBlank()) "\"${favorite.personalNotes}\"" else "Tap \"Edit Diary\" to add personal comments, review records, or sitting recommendations!",
                        fontSize = 12.sp,
                        color = if (favorite.personalNotes.isNotBlank()) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                        maxLines = 2,
                        lineHeight = 16.sp,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}
