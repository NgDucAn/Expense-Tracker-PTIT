package com.ptit.expensetracker.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40

    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)



// Using the custom dark colors defined in Color.kt
private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80,
    background = ScreenBackground, // Use custom background
    surface = CardBackground,      // Use custom card background
    onPrimary = Purple40,
    onSecondary = PurpleGrey40,
    onTertiary = Pink40,
    onBackground = TextPrimary,    // Use custom text color
    onSurface = TextPrimary        // Use custom text color on cards/surfaces
)

// You can define a LightColorScheme similarly if needed,
// but the screenshot is dark theme.
// private val LightColorScheme = lightColorScheme(...)

@Composable
fun ExpenseTrackerTheme( // Đặt tên Theme phù hợp với ứng dụng của bạn
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false, // Set to false to enforce our custom theme
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        // Always use our custom DarkColorScheme based on the screenshot
        else -> DarkColorScheme
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = AppColor.Dark.PrimaryColor.containerColor.toArgb() // Match status bar with background
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme // Use dark icons on light status bar and vice-versa
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography, // Assuming you have a Typography.kt file
        content = content
    )
}