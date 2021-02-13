package dev.esnault.bunpyro.android.display.compose

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.Colors
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

private val Blue600 = Color(0xFF1E88E5)
private val Blue800 = Color(0xFF1565C0)
private val LightBlueA200 = Color(0xFF40C4FF)
private val LightBlueA400 = Color(0xFF00B0FF)

private val LightColors = lightColors(
    primary = Blue600,
    primaryVariant = Blue800,
    secondary = LightBlueA200,
    secondaryVariant = LightBlueA400
)

private val DarkColors = darkColors(
    primary = Blue600,
    primaryVariant = Blue800,
    secondary = LightBlueA200
)

private val body2 = TextStyle(
    fontWeight = FontWeight.Light,
    fontSize = 14.sp,
    letterSpacing = 0.25.sp
)

@Composable
fun AppTheme(content: @Composable () -> Unit) {
    val darkTheme = isSystemInDarkTheme()
    MaterialTheme(
        colors = if (darkTheme) DarkColors else LightColors,
        typography = MaterialTheme.typography.copy(body2 = body2),
        content = content
    )
}
