package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val CyberDarkColorScheme = darkColorScheme(
  primary = CyberCyan,
  onPrimary = Color.Black,
  secondary = HologramBlue,
  onSecondary = Color.Black,
  tertiary = NeonPink,
  onTertiary = Color.White,
  background = ObsidianBackground,
  onBackground = GlassTextPrimary,
  surface = ObsidianSurface,
  onSurface = GlassTextPrimary,
  surfaceVariant = ObsidianSurfaceElevated,
  onSurfaceVariant = GlassTextSecondary,
  outline = GlassWhiteBorder
)

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  dynamicColor: Boolean = false, // Force our premium theme by default
  content: @Composable () -> Unit,
) {
  // Always use our ultra-futuristic CyberDarkColorScheme to ensure the 2040 premium vibe
  val colorScheme = CyberDarkColorScheme

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
