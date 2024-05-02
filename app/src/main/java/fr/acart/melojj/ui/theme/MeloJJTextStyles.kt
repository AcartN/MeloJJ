package fr.acart.melojj.ui.theme

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.sp
import fr.acart.melojj.R

val LocalMeloJJTextStyles = staticCompositionLocalOf<MeloJJTextStyles> {
    error("No MeloJJTextStyles provided")
}

@Immutable
data class MeloJJTextStyles(
    val h1: TextStyle,
    val h2: TextStyle,
    val h3: TextStyle,
    val h4: TextStyle,
    val h5: TextStyle,
    val h6: TextStyle,
    val action1: TextStyle,
    val action2: TextStyle,
    val action3: TextStyle,
    val textStrong1: TextStyle,
    val textStrong2: TextStyle,
    val textStrong3: TextStyle,
    val textStrong4: TextStyle,
    val textRegular1: TextStyle,
    val textRegular2: TextStyle,
    val textRegular3: TextStyle,
    val textRegular4: TextStyle,
) {
    val groupedEntries by lazy {
        mapOf(
            "Headline" to listOf(h1, h2, h3, h4, h5, h6),
            "Action" to listOf(action1, action2, action3),
            "Text Strong" to listOf(textStrong1, textStrong2, textStrong3, textStrong4),
            "Text Regular" to listOf(textRegular1, textRegular2, textRegular3, textRegular4),
        )
    }

    companion object {
        operator fun invoke(): MeloJJTextStyles {
            val latoFontFamily = FontFamily(
                Font(R.font.lato, FontWeight.Normal),
                Font(R.font.lato_italic, FontWeight.Normal, FontStyle.Italic),
                Font(R.font.lato_bold, FontWeight.Bold),
                Font(R.font.lato_semibold, FontWeight.SemiBold),
                Font(R.font.lato_semibold_italic, FontWeight.SemiBold, FontStyle.Italic),
            )
            val tisaFontFamily = FontFamily(
                Font(R.font.tisa, FontWeight.Normal),
                Font(R.font.tisa_bold, FontWeight.Bold),
            )
            val headline = TextStyle(
                fontFamily = tisaFontFamily,
                fontWeight = FontWeight.SemiBold,
                platformStyle = PlatformTextStyle(includeFontPadding = false),
            )
            val action = TextStyle(
                fontFamily = latoFontFamily,
                fontWeight = FontWeight.SemiBold,
                platformStyle = PlatformTextStyle(includeFontPadding = false),
            )
            val textStrong = TextStyle(
                fontFamily = tisaFontFamily,
                fontWeight = FontWeight.SemiBold,
                platformStyle = PlatformTextStyle(includeFontPadding = false),
            )
            val textRegular = TextStyle(
                fontFamily = latoFontFamily,
                fontWeight = FontWeight.Normal,
                platformStyle = PlatformTextStyle(includeFontPadding = false),
            )
            return MeloJJTextStyles(
                h1 = headline.copy(fontSize = 33.sp),
                h2 = headline.copy(fontSize = 28.sp),
                h3 = headline.copy(fontSize = 23.sp),
                h4 = headline.copy(fontSize = 18.sp),
                h5 = headline.copy(fontSize = 16.sp),
                h6 = headline.copy(fontSize = 14.sp),
                action1 = action.copy(fontSize = 14.sp),
                action2 = action.copy(fontSize = 12.sp),
                action3 = action.copy(fontSize = 10.sp),
                textStrong1 = textStrong.copy(fontSize = 18.sp),
                textStrong2 = textStrong.copy(fontSize = 16.sp),
                textStrong3 = textStrong.copy(fontSize = 14.sp),
                textStrong4 = textStrong.copy(fontSize = 12.sp),
                textRegular1 = textRegular.copy(fontSize = 18.sp),
                textRegular2 = textRegular.copy(fontSize = 16.sp),
                textRegular3 = textRegular.copy(fontSize = 14.sp),
                textRegular4 = textRegular.copy(fontSize = 12.sp),
            )
        }
    }
}

private class MeloJJTextStylesPreviewProvider : PreviewParameterProvider<Pair<String, TextStyle>> {
    override val values: Sequence<Pair<String, TextStyle>> = MeloJJTextStyles()
        .groupedEntries
        .map { (groupName, textStyles) ->
            textStyles.map { textStyle ->
                "$groupName ${textStyle.fontSize.value.toInt()}sp" to textStyle
            }
        }
        .flatten()
        .asSequence()
}

@Preview
@Composable
private fun MeloJJTextStylesPreview(
    @PreviewParameter(MeloJJTextStylesPreviewProvider::class) namedTextStyle: Pair<String, TextStyle>,
) {
    Text(
        text = namedTextStyle.first,
        style = namedTextStyle.second,
        color = Color.White,
    )
}
