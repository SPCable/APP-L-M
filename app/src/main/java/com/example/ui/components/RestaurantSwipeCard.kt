package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.Restaurant
import kotlinx.coroutines.launch
import kotlin.math.abs

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RestaurantSwipeCard(
    restaurant: Restaurant,
    onSwipeLeft: () -> Unit,
    onSwipeRight: () -> Unit,
    onDetailClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    
    // Physical state trackers for dragging
    val offsetX = remember { Animatable(0f) }
    val offsetY = remember { Animatable(0f) }

    // Direct memory state variables for gesture tracking (bypasses any main thread congestion)
    var isDragging by remember { mutableStateOf(false) }
    var dragX by remember { mutableStateOf(0f) }
    var dragY by remember { mutableStateOf(0f) }

    // Screen info to compute escape speed and swipe limits
    val configuration = LocalConfiguration.current
    val density = LocalDensity.current
    val screenWidthPx = with(density) { configuration.screenWidthDp.dp.toPx() }
    val swipeThreshold = screenWidthPx * 0.35f

    val currentX = if (isDragging) dragX else offsetX.value
    val currentY = if (isDragging) dragY else offsetY.value

    val rotation = (currentX / screenWidthPx) * 20f
    val scale = 1f

    // Calculate stamp alphas
    val likeAlpha = (currentX / swipeThreshold).coerceIn(0f, 1f)
    val nopeAlpha = (-currentX / swipeThreshold).coerceIn(0f, 1f)

    Card(
        modifier = modifier
            .fillMaxSize()
            .graphicsLayer {
                translationX = currentX
                translationY = currentY
                rotationZ = rotation
                scaleX = scale
                scaleY = scale
            }
            .pointerInput(restaurant.id) {
                detectDragGestures(
                    onDragStart = {
                        isDragging = true
                        dragX = offsetX.value
                        dragY = offsetY.value
                    },
                    onDragEnd = {
                        isDragging = false
                        coroutineScope.launch {
                            offsetX.snapTo(dragX)
                            offsetY.snapTo(dragY)
                            
                            if (dragX > swipeThreshold) {
                                // Swipe Right - LIKE
                                launch {
                                    offsetX.animateTo(
                                        targetValue = screenWidthPx * 1.5f,
                                        animationSpec = tween(300)
                                    )
                                    onSwipeRight()
                                }
                            } else if (dragX < -swipeThreshold) {
                                // Swipe Left - DISLIKE / REJECT
                                launch {
                                    offsetX.animateTo(
                                        targetValue = -screenWidthPx * 1.5f,
                                        animationSpec = tween(300)
                                    )
                                    onSwipeLeft()
                                }
                            } else {
                                // Snap back home
                                launch { offsetX.animateTo(0f, spring()) }
                                launch { offsetY.animateTo(0f, spring()) }
                            }
                        }
                    },
                    onDragCancel = {
                        isDragging = false
                        coroutineScope.launch {
                            offsetX.snapTo(dragX)
                            offsetY.snapTo(dragY)
                            launch { offsetX.animateTo(0f, spring()) }
                            launch { offsetY.animateTo(0f, spring()) }
                        }
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        dragX += dragAmount.x
                        dragY += dragAmount.y
                    }
                )
            },
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Background cover food image
            AsyncImage(
                model = restaurant.imageUrl,
                contentDescription = restaurant.name,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(24.dp)),
                contentScale = ContentScale.Crop
            )

            // Dynamic Gradient Overlay for high-end contrast
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.2f),
                                Color.Black.copy(alpha = 0.85f)
                            ),
                            startY = 400f
                        )
                    )
            )

            // Swipe indicator stamps overlaying upper corners
            if (likeAlpha > 0.05f) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(24.dp)
                        .graphicsLayer {
                            alpha = likeAlpha
                            rotationZ = -12f
                        }
                        .border(4.dp, Color(0xFF4CAF50), RoundedCornerShape(12.dp))
                        .background(Color.Black.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = "SAVE",
                        color = Color(0xFF4CAF50),
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 2.sp
                    )
                }
            }

            if (nopeAlpha > 0.05f) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(24.dp)
                        .graphicsLayer {
                            alpha = nopeAlpha
                            rotationZ = 12f
                        }
                        .border(4.dp, Color(0xFFEF5350), RoundedCornerShape(12.dp))
                        .background(Color.Black.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = "SKIP",
                        color = Color(0xFFEF5350),
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 2.sp
                    )
                }
            }

            // High-polished overlay info panel bottom
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                // Cuisine badge
                Surface(
                    shape = RoundedCornerShape(50.dp),
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.85f),
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    Text(
                        text = restaurant.cuisine,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }

                // Name and Price
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = restaurant.name,
                        color = Color.White,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.ExtraBold,
                        modifier = Modifier.weight(1f)
                    )
                    
                    Text(
                        text = restaurant.priceRange,
                        color = Color(0xFFFFD700),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(start = 12.dp)
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Rating & Reviews & Distance Label
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .background(Color.White.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Rating",
                            tint = Color(0xFFFFD700),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${restaurant.rating}",
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Text(
                        text = "(${restaurant.reviewsCount} reviews)",
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 14.sp
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.LocationOn,
                            contentDescription = "Distance",
                            tint = Color.White.copy(alpha = 0.8f),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            text = "${restaurant.distance} mi",
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Short tagline / description
                Text(
                    text = restaurant.description,
                    color = Color.White.copy(alpha = 0.85f),
                    fontSize = 14.sp,
                    maxLines = 2,
                    lineHeight = 18.sp,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                // Navigation or dialog button
                Button(
                    onClick = onDetailClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White.copy(alpha = 0.25f),
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .testTag("detail_button")
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Info,
                        contentDescription = "Details",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Read Reviews & Info", fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

@Composable
fun TinderActionBar(
    onDislike: () -> Unit,
    onReset: () -> Unit,
    onLike: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Dislike/Skip Button: bg-[#1E1F22], border-[#FFB4AB], text-[#FFB4AB]
        Surface(
            onClick = onDislike,
            shape = RoundedCornerShape(50.dp),
            color = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.error,
            modifier = Modifier
                .size(56.dp)
                .border(1.dp, MaterialTheme.colorScheme.error, RoundedCornerShape(50.dp))
                .testTag("dislike_button"),
            shadowElevation = 8.dp
        ) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                Icon(
                    imageVector = Icons.Outlined.Close,
                    contentDescription = "Skip",
                    modifier = Modifier.size(28.dp)
                )
            }
        }

        Spacer(modifier = Modifier.width(32.dp))

        // Like Button: bg-[#D0BCFF], text-[#381E72], w-16 h-16
        Surface(
            onClick = onLike,
            shape = RoundedCornerShape(50.dp),
            color = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier
                .size(64.dp)
                .testTag("like_button"),
            shadowElevation = 12.dp
        ) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                Icon(
                    imageVector = Icons.Outlined.Favorite,
                    contentDescription = "Save Like",
                    modifier = Modifier.size(32.dp)
                )
            }
        }

        Spacer(modifier = Modifier.width(32.dp))

        // Reset / Refill / Bookmark Button: bg-[#1E1F22], border-[#D0BCFF], text-[#D0BCFF]
        Surface(
            onClick = onReset,
            shape = RoundedCornerShape(50.dp),
            color = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .size(56.dp)
                .border(1.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(50.dp))
                .testTag("reset_button"),
            shadowElevation = 8.dp
        ) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                Icon(
                    imageVector = Icons.Outlined.Info,
                    contentDescription = "Reset Swipe Deck",
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}
