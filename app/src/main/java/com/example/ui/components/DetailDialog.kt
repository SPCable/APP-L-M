package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.example.data.FavoriteRestaurant
import com.example.data.Restaurant
import com.example.data.Review

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailDialog(
    restaurant: Restaurant,
    favoriteInfo: FavoriteRestaurant?,
    onDismiss: () -> Unit,
    onSavePersonalData: (notes: String, rating: Double?) -> Unit,
    modifier: Modifier = Modifier
) {
    var personalNotes by remember(favoriteInfo) { mutableStateOf(favoriteInfo?.personalNotes ?: "") }
    var personalRating by remember(favoriteInfo) { mutableStateOf(favoriteInfo?.personalRating) }
    var isEditingPersonal by remember { mutableStateOf(false) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = modifier
                .fillMaxWidth(0.94f)
                .fillMaxHeight(0.85f)
                .clip(RoundedCornerShape(28.dp)),
            color = MaterialTheme.colorScheme.background,
            tonalElevation = 6.dp
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Image Header with absolute clear close button
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                ) {
                    AsyncImage(
                        model = restaurant.imageUrl,
                        contentDescription = restaurant.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )

                    // Close Button
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(12.dp)
                            .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                            .size(36.dp)
                            .testTag("dialog_close_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    // Price and Cuisine Overlay tags
                    Row(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                        ) {
                            Text(
                                text = restaurant.cuisine,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFFFFD700),
                            contentColor = Color.Black
                        ) {
                            Text(
                                text = restaurant.priceRange,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                // Scrollable Content
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(18.dp)
                ) {
                    // Header Name & distance
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = restaurant.name,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = restaurant.address,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f),
                            modifier = Modifier.padding(start = 12.dp)
                        ) {
                            Text(
                                text = "${restaurant.distance} mi",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Description text
                    Text(
                        text = "About",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = restaurant.description,
                        style = MaterialTheme.typography.bodyMedium,
                        lineHeight = 22.sp,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Local Persistence: Personal Diaries Tab if favorited
                    if (favoriteInfo != null) {
                        ElevatedCard(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.elevatedCardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 20.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "My Personal Dining Note",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    IconButton(onClick = { isEditingPersonal = !isEditingPersonal }) {
                                        Icon(
                                            imageVector = Icons.Outlined.Edit,
                                            contentDescription = "Edit Note",
                                            tint = MaterialTheme.colorScheme.secondary,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }

                                if (isEditingPersonal) {
                                    // Interactive Personal Rating
                                    Text(
                                        text = "My Rating:",
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Row(
                                        modifier = Modifier.padding(vertical = 4.dp),
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        for (i in 1..5) {
                                            Icon(
                                                imageVector = if (i <= (personalRating ?: 0.0)) Icons.Filled.Star else Icons.Outlined.Star,
                                                contentDescription = "Star $i",
                                                tint = Color(0xFFFFD700),
                                                modifier = Modifier
                                                    .size(28.dp)
                                                    .clickable { personalRating = i.toDouble() }
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))

                                    OutlinedTextField(
                                        value = personalNotes,
                                        onValueChange = { personalNotes = it },
                                        placeholder = { Text("E.g., Great pad thai, sit by the window!") },
                                        modifier = Modifier.fillMaxWidth().testTag("personal_notes_input"),
                                        textStyle = MaterialTheme.typography.bodyMedium,
                                        shape = RoundedCornerShape(10.dp)
                                    )

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Button(
                                        onClick = {
                                            onSavePersonalData(personalNotes, personalRating)
                                            isEditingPersonal = false
                                        },
                                        modifier = Modifier.align(Alignment.End).testTag("save_personal_button")
                                    ) {
                                        Text("Save Notes")
                                    }
                                } else {
                                    // Read-only State
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(vertical = 4.dp)
                                    ) {
                                        Text(
                                            text = "My Rating: ",
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.Bold
                                        )
                                        if (personalRating != null) {
                                            Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                                                for (i in 1..5) {
                                                    Icon(
                                                        imageVector = if (i <= personalRating!!) Icons.Filled.Star else Icons.Outlined.Star,
                                                        contentDescription = null,
                                                        tint = Color(0xFFFFD700),
                                                        modifier = Modifier.size(16.dp)
                                                    )
                                                }
                                            }
                                        } else {
                                            Text(
                                                text = "Not rated yet",
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(4.dp))

                                    Text(
                                        text = if (personalNotes.isNotBlank()) personalNotes else "No personal notes added yet. Tap edit icon above to record your thoughts!",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = if (personalNotes.isNotBlank()) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                                    )
                                }
                            }
                        }
                    }

                    // Reviews title & rating card
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Reviews (${restaurant.reviewsCount})",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = "Star",
                                tint = Color(0xFFFFD700),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${restaurant.rating}",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Reviews list
                    if (restaurant.reviews.isEmpty()) {
                        Text(
                            text = "No reviews available yet.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                        )
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            restaurant.reviews.forEach { r ->
                                ReviewItem(review = r)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ReviewItem(review: Review, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
        )
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = review.author.take(1).uppercase(),
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = review.author,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
                
                Text(
                    text = review.date,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Row(horizontalArrangement = Arrangement.spacedBy(1.dp)) {
                    for (i in 1..5) {
                        Icon(
                            imageVector = if (i <= review.rating) Icons.Filled.Star else Icons.Outlined.Star,
                            contentDescription = null,
                            tint = Color(0xFFFFD700),
                            modifier = Modifier.size(12.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "${review.rating}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = review.comment,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.85f),
                lineHeight = 18.sp
            )
        }
    }
}
