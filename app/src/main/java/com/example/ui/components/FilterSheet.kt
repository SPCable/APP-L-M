package com.example.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.RestaurantViewModel
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterDialog(
    currentRating: Double,
    currentPrices: Set<String>,
    currentMaxDistance: Double,
    onDismiss: () -> Unit,
    onApply: (rating: Double, prices: Set<String>, distance: Double) -> Unit,
    modifier: Modifier = Modifier
) {
    // Local copy of selections until "Apply" is pressed
    var selectedRating by remember { mutableStateOf(currentRating) }
    var selectedPrices by remember { mutableStateOf(currentPrices) }
    var selectedDistance by remember { mutableStateOf(currentMaxDistance) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .clip(RoundedCornerShape(24.dp)),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth()
            ) {
                // Header with Title and Close Button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Filter Options",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close Filters",
                            tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Section 1: Rating (Any, 4.0+, 4.5+)
                Text(
                    text = "Minimum Rating",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val ratings = listOf(0.0, 4.0, 4.5, 4.7)
                    val ratingLabels = listOf("Any", "4.0+ ★", "4.5+ ★", "4.7+ ★")
                    
                    ratings.forEachIndexed { index, value ->
                        val isSelected = selectedRating == value
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedRating = value },
                            label = { Text(ratingLabels[index]) },
                            modifier = Modifier.testTag("filter_rating_${value.toString().replace('.', '_')}")
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Section 2: Price multi-select ($, $$, $$$)
                Text(
                    text = "Price Level",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val priceOptions = listOf("$", "$$", "$$$")
                    val priceLabels = listOf("Budget ($)", "Moderate ($$)", "Gourmet ($$$)")

                    priceOptions.forEachIndexed { index, item ->
                        val isSelected = selectedPrices.contains(item)
                        FilterChip(
                            selected = isSelected,
                            onClick = {
                                selectedPrices = if (isSelected) {
                                    selectedPrices - item
                                } else {
                                    selectedPrices + item
                                }
                            },
                            label = { Text(priceLabels[index]) },
                            modifier = Modifier.testTag("filter_price_chip_$index")
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Section 3: Distance slider (0.5 to 10.0 miles)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Maximum Distance",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "${(selectedDistance * 10).roundToInt() / 10.0} mi",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Slider(
                    value = selectedDistance.toFloat(),
                    onValueChange = { selectedDistance = it.toDouble() },
                    valueRange = 0.5f..10.0f,
                    steps = 19, // 0.5 mile steps
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("distance_slider")
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Operational Action buttons: Clear or Apply
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            selectedRating = 0.0
                            selectedPrices = emptySet()
                            selectedDistance = 5.0
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("reset_filters_button")
                    ) {
                        Text("Reset All")
                    }

                    Button(
                        onClick = {
                            onApply(selectedRating, selectedPrices, selectedDistance)
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("apply_filters_button")
                    ) {
                        Text("Apply Filters")
                    }
                }
            }
        }
    }
}
