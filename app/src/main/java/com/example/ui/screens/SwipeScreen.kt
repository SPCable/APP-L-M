package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.Restaurant
import com.example.data.RestaurantData
import com.example.ui.components.DetailDialog
import com.example.ui.components.RestaurantSwipeCard
import com.example.ui.components.TinderActionBar
import com.example.ui.theme.MyApplicationTheme

@Composable
fun SwipeScreen(
    swipeDeck: List<List<Restaurant>>, // Passing double-wrap or outer list so it maintains state nicely
    deckRestaurants: List<Restaurant>,
    onSwipeLeft: (Restaurant) -> Unit,
    onSwipeRight: (Restaurant) -> Unit,
    onResetDeck: () -> Unit,
    favoriteMap: Map<String, com.example.data.FavoriteRestaurant>, // to check if favorite inside detail dialog
    onSavePersonalReview: (id: String, notes: String, rating: Double?) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedRestaurantForDetail by remember { mutableStateOf<Restaurant?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // App slogan / Subtitle
        Text(
            text = "LỤM",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.5.sp,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            if (deckRestaurants.isEmpty()) {
                // Out of Cards Empty State
                Card(
                    modifier = Modifier
                        .fillMaxWidth(0.92f)
                        .fillMaxHeight(0.85f)
                        .testTag("swipe_empty_card"),
                    shape = RoundedCornerShape(24.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .background(
                                    Brush.linearGradient(
                                        colors = listOf(
                                            MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                                            MaterialTheme.colorScheme.secondary.copy(alpha = 0.3f)
                                        )
                                    ),
                                    RoundedCornerShape(50.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Restaurant,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(40.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        Text(
                            text = "No More Restaurants!",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "You've swiped on all available spots in the area that match your rating, distance, or price filters.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                            textAlign = TextAlign.Center,
                            lineHeight = 20.sp
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        Button(
                            onClick = onResetDeck,
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            modifier = Modifier
                                .fillMaxWidth(0.8f)
                                .height(48.dp)
                                .testTag("reset_deck_button")
                        ) {
                            Icon(imageVector = Icons.Default.Refresh, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Refill Swipe Deck", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            } else {
                // Stack of physical cards
                // Draw bottom cards first, then top card
                val maxCardsToShow = 3
                val cards = deckRestaurants.take(maxCardsToShow).asReversed()

                cards.forEachIndexed { revIndex, restaurant ->
                    // Index from top (0 is top card, 1 is under it, etc.)
                    val indexFromTop = (cards.size - 1) - revIndex

                    key(restaurant.id) {
                        if (indexFromTop == 0) {
                            // The interactive active top card
                            RestaurantSwipeCard(
                                restaurant = restaurant,
                                onSwipeLeft = { onSwipeLeft(restaurant) },
                                onSwipeRight = { onSwipeRight(restaurant) },
                                onDetailClick = { selectedRestaurantForDetail = restaurant },
                                modifier = Modifier
                                    .fillMaxWidth(0.92f)
                                    .fillMaxHeight(0.85f)
                                    .testTag("restaurant_swipe_card_${restaurant.id}")
                            )
                        } else {
                            // Background placeholder cards (slightly shrunk and translated downward)
                            val translationYOffset = (indexFromTop * 14).dp
                            val scaleFactor = 1f - (indexFromTop * 0.04f)

                            Card(
                                modifier = Modifier
                                    .fillMaxWidth(0.92f)
                                    .fillMaxHeight(0.85f)
                                    .offset(y = translationYOffset)
                                    .scale(scaleFactor)
                                    .align(Alignment.Center),
                                shape = RoundedCornerShape(24.dp),
                                elevation = CardDefaults.cardElevation(defaultElevation = (4 - indexFromTop).dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                            ) {
                                // Empty box just to provide stacking aesthetics while sleeping
                                Box(modifier = Modifier.fillMaxSize())
                            }
                        }
                    }
                }
            }
        }

        // Tinder Circular floating Control Action Bar
        if (deckRestaurants.isNotEmpty()) {
            val topRestaurant = deckRestaurants.first()
            TinderActionBar(
                onDislike = { onSwipeLeft(topRestaurant) },
                onReset = onResetDeck,
                onLike = { onSwipeRight(topRestaurant) },
                modifier = Modifier.padding(top = 12.dp)
            )
        } else {
            Spacer(modifier = Modifier.height(84.dp))
        }

        // Details dialog popup
        selectedRestaurantForDetail?.let { restaurant ->
            DetailDialog(
                restaurant = restaurant,
                favoriteInfo = favoriteMap[restaurant.id],
                onDismiss = { selectedRestaurantForDetail = null },
                onSavePersonalData = { notes, rating ->
                    onSavePersonalReview(restaurant.id, notes, rating)
                }
            )
        }
    }
}

@Preview(showBackground = true, name = "Swipe Screen - Active")
@Composable
fun SwipeScreenActivePreview() {
    val sampleRestaurants = RestaurantData.localRestaurants
    MyApplicationTheme {
        SwipeScreen(
            swipeDeck = listOf(sampleRestaurants),
            deckRestaurants = sampleRestaurants,
            onSwipeLeft = {},
            onSwipeRight = {},
            onResetDeck = {},
            favoriteMap = emptyMap(),
            onSavePersonalReview = { _, _, _ -> }
        )
    }
}

@Preview(showBackground = true, name = "Swipe Screen - Empty")
@Composable
fun SwipeScreenEmptyPreview() {
    MyApplicationTheme {
        SwipeScreen(
            swipeDeck = emptyList(),
            deckRestaurants = emptyList(),
            onSwipeLeft = {},
            onSwipeRight = {},
            onResetDeck = {},
            favoriteMap = emptyMap(),
            onSavePersonalReview = { _, _, _ -> }
        )
    }
}
