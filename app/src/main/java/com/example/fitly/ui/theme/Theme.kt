package com.example.fitly.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = primaryColor,
    secondary = secondaryColor,
    background = dBackgroundColor,
    onBackground = dTextColor,
    onSurface = dOnSurfaceColor,
    error = error,
    surface = dOnSurfaceColor
)

private val LightColorScheme = lightColorScheme(
    primary = primaryColor,
    secondary = secondaryColor,
    background = lBackgroundColor,
    onBackground = lTextColor,
    onSurface = lOnSurfaceColor,
    error = error,
    surface = moreDark


)

@Composable
fun FitlyTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
//    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
//         Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
//            val context = LocalContext.current
//            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
//        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}