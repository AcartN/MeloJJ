package fr.acart.melojj.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

val LocalMeloJJColors = staticCompositionLocalOf<MeloJJColors> {
    error("No MeloJJColors provided")
}

@Immutable
data class MeloJJColors(
    val background: MeloJJBackgroundColors,
    val content: MeloJJContentColors,
) {
    companion object {
        operator fun invoke() = MeloJJColors(
            background = MeloJJBackgroundColors(
                primary = Color(BackgroundPrimary),
                secondary = Color(BackgroundSecondary),
                tertiary = Color(BackgroundTertiary),
                accent = Color(BackgroundAccent),
                danger = Color(BackgroundDanger),
            ),
            content = MeloJJContentColors(
                primary = Color(ContentPrimary),
                secondary = Color(ContentSecondary),
                tertiary = Color(ContentTertiary),
                accentPrimary = Color(ContentAccentPrimary),
                accentSecondary = Color(ContentAccentSecondary),
                danger = Color(ContentDanger),
            ),
        )
    }
}

@Immutable
data class MeloJJBackgroundColors(
    val primary: Color,
    val secondary: Color,
    val tertiary: Color,
    val accent: Color,
    val danger: Color,
) {
    val entries by lazy {
        mapOf(
            "Primary" to primary,
            "Secondary" to secondary,
            "Tertiary" to tertiary,
            "Accent" to accent,
            "Danger" to danger,
        )
    }
}

@Immutable
data class MeloJJContentColors(
    val primary: Color,
    val secondary: Color,
    val tertiary: Color,
    val accentPrimary: Color,
    val accentSecondary: Color,
    val danger: Color,
) {
    val entries by lazy {
        mapOf(
            "Primary" to primary,
            "Secondary" to secondary,
            "Tertiary" to tertiary,
            "Accent Primary" to accentPrimary,
            "Accent Secondary" to accentSecondary,
            "Danger" to danger,
        )
    }
}

@Preview
@Composable
private fun MeloJJColorsPreview(
    @PreviewParameter(ColorPreviewParameterProvider::class) namedColor: Pair<String, Color>,
) {
    Column {
        Text(
            text = namedColor.first,
            fontSize = 14.sp,
            color = Color.White,
        )
        Text(
            text = namedColor.second.value.toHexString(),
            fontSize = 12.sp,
            color = Color.Gray,
        )
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(namedColor.second),
        )
    }
}

private fun ULong.toHexString() = toString(16).dropLast(8)

private class ColorPreviewParameterProvider : PreviewParameterProvider<Pair<String, Color>> {
    override val values = run {
        val meloJJColors = MeloJJColors()
        meloJJColors.background.entries.map { (name, color) ->
            ("Background $name" to color)
        } +
                meloJJColors.content.entries.map { (name, color) ->
                    ("Content $name" to color)
                }
    }.asSequence()
}