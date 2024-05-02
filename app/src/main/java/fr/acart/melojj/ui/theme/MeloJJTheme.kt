package fr.acart.melojj.ui.theme

import android.app.Activity
import android.graphics.Color
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

@Composable
fun MeloJJTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    colorScheme: ColorScheme = if (darkTheme) darkColorScheme() else lightColorScheme(),
    typography: Typography = Typography(),
    shapes: Shapes = Shapes(),
    content: @Composable () -> Unit,
) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = Color.TRANSPARENT
            window.navigationBarColor = Color.TRANSPARENT
            WindowCompat.setDecorFitsSystemWindows(window, false)
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    val meloJJColors = MeloJJColors()
    val meloJJSpacings = MeloJJSpacings()
    val meloJJShapes = MeloJJShapes()
    val meloJJTextStyles = MeloJJTextStyles()
    MaterialTheme(
        colorScheme = colorScheme,
        typography = typography,
        shapes = shapes,
    ) {
        CompositionLocalProvider(
            LocalMeloJJColors provides meloJJColors,
            LocalMeloJJSpacings provides meloJJSpacings,
            LocalMeloJJShapes provides meloJJShapes,
            LocalMeloJJTextStyles provides meloJJTextStyles,
            LocalTextStyle provides meloJJTextStyles.textRegular1,
            LocalContentColor provides meloJJColors.content.primary,
        ) {
            content()
        }
    }
}

object MeloJJTheme {

    val colors: MeloJJColors
        @Composable get() = LocalMeloJJColors.current

    val spacings: MeloJJSpacings
        @Composable get() = LocalMeloJJSpacings.current

    val shapes: MeloJJShapes
        @Composable get() = LocalMeloJJShapes.current

    val textStyles: MeloJJTextStyles
        @Composable get() = LocalMeloJJTextStyles.current

}