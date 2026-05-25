package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Restaurant
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun LoginScreen(
    isLoading: Boolean,
    errorMessage: String?,
    onGoogleSignInClick: () -> Unit,
    onDemoSignInClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Beautiful ambient breathing animation for background glow
    val infiniteTransition = rememberInfiniteTransition(label = "ambientGlow")
    val scaleFactor by infiniteTransition.animateFloat(
        initialValue = 0.92f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowScale"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF1C0D0A), // Extremely deep amber black
                        Color(0xFF0F0705)  // Near complete dark
                    )
                )
            )
            .testTag("login_screen"),
        contentAlignment = Alignment.Center
    ) {
        // 1. Ambient warm colored circular glows in the background
        Box(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .aspectRatio(1f)
                .align(Alignment.TopCenter)
                .offset(y = (-80).dp)
                .clip(CircleShape)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0xFFE65100).copy(alpha = 0.12f * scaleFactor),
                            Color.Transparent
                        )
                    )
                )
        )

        Box(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .aspectRatio(1f)
                .align(Alignment.BottomCenter)
                .offset(y = 120.dp)
                .clip(CircleShape)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0xFF4CAF50).copy(alpha = 0.08f * scaleFactor),
                            Color.Transparent
                        )
                    )
                )
        )

        // 2. High-Polished Content Overlay
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Spacer to balance layout
            Spacer(modifier = Modifier.height(30.dp))

            // Upper Block: App Title & Branding
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Stylish Icon Logo Badge
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .shadow(16.dp, RoundedCornerShape(24.dp))
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(Color(0xFFE2F9E5), Color(0xFFC8F2CD))
                            ),
                            shape = RoundedCornerShape(24.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Restaurant,
                        contentDescription = "App Logo",
                        tint = Color(0xFF0F3A20),
                        modifier = Modifier.size(42.dp)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Modern "LỤM" Typography
                Text(
                    text = "LỤM",
                    fontSize = 54.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White,
                    letterSpacing = 4.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Captivating Tagline
                Text(
                    text = "Lướt Món Ngon • Lụm Quán Đỉnh",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White.copy(alpha = 0.6f),
                    letterSpacing = 0.5.sp
                )
            }

            // Middle Block: Authentic food stacked visual card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentAlignment = Alignment.Center
            ) {
                // Interactive floating cards to represent Vietnamese dishes
                FoodBadgeCard(
                    title = "🍜 Bún Chả Obama",
                    rating = "4.9",
                    offset = Offset(-30f, -20f),
                    angle = -8f,
                    color = Color(0xFFE2F9E5)
                )
                FoodBadgeCard(
                    title = "🥖 Bánh Mì Dân Tổ",
                    rating = "4.8",
                    offset = Offset(40f, 15f),
                    angle = 6f,
                    color = Color(0xFFFFF3E0)
                )
            }

            // Lower Block: Auth Controls
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Error Alert Box (if present)
                AnimatedVisibility(
                    visible = errorMessage != null,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    errorMessage?.let {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0x22EF5350),
                            border = BorderStroke(1.dp, Color(0x44EF5350)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp)
                        ) {
                            Text(
                                text = it,
                                color = Color(0xFFEF5350),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                    }
                }

                if (isLoading) {
                    CircularProgressIndicator(
                        color = Color(0xFF4CAF50),
                        strokeWidth = 3.dp,
                        modifier = Modifier
                            .size(48.dp)
                            .padding(bottom = 24.dp)
                    )
                } else {
                    // Modern Gmail Google Sign-In Button
                    Surface(
                        onClick = onGoogleSignInClick,
                        shape = RoundedCornerShape(30.dp),
                        color = Color.White,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .shadow(12.dp, RoundedCornerShape(30.dp))
                            .testTag("google_login_button")
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 24.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            GoogleLogoIcon(modifier = Modifier.size(22.dp))
                            Spacer(modifier = Modifier.width(14.dp))
                            Text(
                                text = "Đăng nhập bằng Google",
                                color = Color(0xFF1A1A1A),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Polished offline Demo access button
                    TextButton(
                        onClick = onDemoSignInClick,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("demo_login_button")
                    ) {
                        Text(
                            text = "Trải nghiệm nhanh (Chế độ Demo)",
                            color = Color(0xFF4CAF50),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Footer security terms notice
                Text(
                    text = "Bảo mật & an toàn bởi Firebase Authentication",
                    fontSize = 10.sp,
                    color = Color.White.copy(alpha = 0.35f),
                    fontWeight = FontWeight.Normal
                )
            }
        }
    }
}

@Composable
fun FoodBadgeCard(
    title: String,
    rating: String,
    offset: Offset,
    angle: Float,
    color: Color
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color(0xFF1E1F22).copy(alpha = 0.85f),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f)),
        modifier = Modifier
            .offset(x = offset.x.dp, y = offset.y.dp)
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(16.dp),
                clip = false
            )
            .graphicsLayer {
                rotationZ = angle
            }
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.width(10.dp))
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = color,
                contentColor = Color(0xFF0F3A20)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.Star,
                        contentDescription = "Rating",
                        tint = Color(0xFFFFD700),
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        text = rating,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }
        }
    }
}

@Composable
fun GoogleLogoIcon(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height

        // Draw the Google G logo using custom paths with official colors
        // Red: #EA4335, Yellow: #FBBC05, Green: #34A853, Blue: #4285F4
        val strokeWidth = width * 0.2f
        val center = Offset(width / 2, height / 2)
        val outerRadius = width / 2
        val innerRadius = outerRadius - strokeWidth

        // Red segment (Top-Left Arc)
        drawArc(
            color = Color(0xFFEA4335),
            startAngle = 180f,
            sweepAngle = 125f,
            useCenter = false,
            size = Size(width, height),
            style = Stroke(width = strokeWidth)
        )

        // Yellow segment (Bottom-Left Arc)
        drawArc(
            color = Color(0xFFFBBC05),
            startAngle = 125f,
            sweepAngle = 55f,
            useCenter = false,
            size = Size(width, height),
            style = Stroke(width = strokeWidth)
        )

        // Green segment (Bottom-Right Arc)
        drawArc(
            color = Color(0xFF34A853),
            startAngle = 0f,
            sweepAngle = 125f,
            useCenter = false,
            size = Size(width, height),
            style = Stroke(width = strokeWidth)
        )

        // Blue segment (Top-Right Arc + horizontal line)
        drawArc(
            color = Color(0xFF4285F4),
            startAngle = 305f,
            sweepAngle = 55f,
            useCenter = false,
            size = Size(width, height),
            style = Stroke(width = strokeWidth)
        )

        // Draw blue horizontal bar inside the G
        val barLength = width * 0.45f
        val barThickness = strokeWidth
        drawRect(
            color = Color(0xFF4285F4),
            topLeft = Offset(width / 2, height / 2 - barThickness / 2),
            size = Size(barLength, barThickness)
        )
    }
}
