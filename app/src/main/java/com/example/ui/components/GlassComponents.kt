package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

// Premium Atmospheric Dark Cyber Background Composable
@Composable
fun CyberSpaceBackground(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianBackground)
            .drawBehind {
                // Background radial gradients representing holographic grid elements
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0x1F00FFCC), Color.Transparent),
                        radius = size.width * 0.8f
                    ),
                    center = androidx.compose.ui.geometry.Offset(0f, 0f)
                )
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0x1F00B2FF), Color.Transparent),
                        radius = size.width * 0.8f
                    ),
                    center = androidx.compose.ui.geometry.Offset(size.width, size.height * 0.7f)
                )
            }
    ) {
        content()
    }
}

// Custom Glass Container simulating frosted glass blur look via alpha gradients & fine lines
@Composable
fun FrostedGlassCard(
    modifier: Modifier = Modifier,
    borderColor: Color = GlassWhiteBorder,
    borderWidth: Dp = 1.dp,
    shape: RoundedCornerShape = RoundedCornerShape(16.dp),
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier
            .clip(shape)
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0x1BFFFFFF), // Slight white tint
                        Color(0x0CFFFFFF)
                    )
                )
            )
            .border(borderWidth, borderColor, shape)
            .padding(16.dp)
    ) {
        content()
    }
}

// Interactive glass clickable button
@Composable
fun FrostedGlassButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    text: String,
    glowColor: Color = CyberCyan,
    enabled: Boolean = true,
    testTag: String = "glass_button"
) {
    val alpha = if (enabled) 1f else 0.5f
    Box(
        modifier = modifier
            .testTag(testTag)
            .clip(RoundedCornerShape(12.dp))
            .background(
                if (enabled) {
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0x28FFFFFF),
                            Color(0x12FFFFFF)
                        )
                    )
                } else {
                    Brush.verticalGradient(
                        colors = listOf(Color(0x0FFFFFFF), Color(0x05FFFFFF))
                    )
                }
            )
            .border(
                1.dp,
                if (enabled) glowColor.copy(alpha = 0.5f) else GlassWhiteBorder,
                RoundedCornerShape(12.dp)
            )
            .clickable(enabled = enabled, onClick = onClick)
            .padding(vertical = 12.dp, horizontal = 24.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = if (enabled) glowColor else GlassTextSecondary,
            style = TextStyle(
                fontFamily = FontFamily.SansSerif,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                shadow = if (enabled) Shadow(color = glowColor, blurRadius = 8f) else null
            ),
            textAlign = TextAlign.Center
        )
    }
}

// Neon-glowing Mood Ring avatar container
@Composable
fun MoodRingAvatar(
    modifier: Modifier = Modifier,
    mood: String, // "Calm", "Highly Excited", "Cortisol-Stressed", "Me"
    initials: String,
    avatarColor: Color,
    size: Dp = 56.dp,
    onClick: (() -> Unit)? = null
) {
    val ringColor = when (mood) {
        "Calm" -> MoodCalmColor
        "Highly Excited" -> MoodExcitedColor
        "Cortisol-Stressed" -> MoodStressedColor
        else -> MoodMeColor
    }

    // Breathing pulse effect for 2040 futuristic aesthetics
    val infiniteTransition = rememberInfiniteTransition(label = "mood_ring_pulse")
    val animatedRadiusOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 4f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1500, easing = LinearOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "radius_pulse"
    )

    val animatedAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha_pulse"
    )

    Box(
        modifier = modifier
            .size(size + 12.dp)
            .clickable(enabled = onClick != null) { onClick?.invoke() },
        contentAlignment = Alignment.Center
    ) {
        // Draw the neon mood glowing ring using Canvas
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeWidth = 3.dp.toPx()
            val diameter = size.toPx() + animatedRadiusOffset.dp.toPx()
            drawCircle(
                color = ringColor.copy(alpha = animatedAlpha),
                radius = diameter / 2f,
                style = Stroke(width = strokeWidth)
            )
        }

        // Inner solid background avatar
        Box(
            modifier = Modifier
                .size(size)
                .clip(CircleShape)
                .background(avatarColor.copy(alpha = 0.25f))
                .border(1.5.dp, ringColor.copy(alpha = 0.8f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = initials,
                color = ringColor,
                fontWeight = FontWeight.Bold,
                fontSize = (size.value * 0.35f).sp,
                style = TextStyle(
                    shadow = Shadow(color = ringColor, blurRadius = 4f)
                )
            )
        }
    }
}

// Styled futuristic title heading
@Composable
fun HolographicTitle(
    text: String,
    modifier: Modifier = Modifier,
    fontSize: Int = 22,
    align: TextAlign = TextAlign.Start
) {
    Text(
        text = text.uppercase(),
        modifier = modifier,
        color = CyberCyan,
        style = TextStyle(
            fontSize = fontSize.sp,
            fontWeight = FontWeight.ExtraBold,
            fontFamily = FontFamily.SansSerif,
            shadow = Shadow(color = CyberCyan.copy(alpha = 0.8f), blurRadius = 12f)
        ),
        textAlign = align
    )
}
