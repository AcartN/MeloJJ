package fr.acart.melojj.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp

val LocalMeloJJShapes = staticCompositionLocalOf<MeloJJShapes> {
    error("No MeloJJShapes provided")
}

@Immutable
data class MeloJJShapes(
    val shapeNull: Shape = RoundedCornerShape(0.dp),
    val shapeXxs: Shape = RoundedCornerShape(2.dp),
    val shapeXs: Shape = RoundedCornerShape(4.dp),
    val shapeSm: Shape = RoundedCornerShape(8.dp),
    val shapeMd: Shape = RoundedCornerShape(12.dp),
    val shapeLg: Shape = RoundedCornerShape(16.dp),
    val shapeXl: Shape = RoundedCornerShape(24.dp),
    val shapeCircle: Shape = CircleShape,
) {
    val entries by lazy {
        mapOf(
            "Null" to shapeNull,
            "XXS" to shapeXxs,
            "XS" to shapeXs,
            "SM" to shapeSm,
            "MD" to shapeMd,
            "LG" to shapeLg,
            "XL" to shapeXl,
            "Circle" to shapeCircle,
        )
    }

    companion object {
        operator fun invoke() = MeloJJShapes(
            shapeNull = RoundedCornerShape(0.dp),
            shapeXxs = RoundedCornerShape(2.dp),
            shapeXs = RoundedCornerShape(4.dp),
            shapeSm = RoundedCornerShape(8.dp),
            shapeMd = RoundedCornerShape(12.dp),
            shapeLg = RoundedCornerShape(16.dp),
            shapeXl = RoundedCornerShape(24.dp),
            shapeCircle = CircleShape,
        )
    }
}

private class MeloJJShapesPreviewProvider : PreviewParameterProvider<Pair<String, Shape>> {
    override val values = MeloJJShapes().entries.toList().asSequence()
}

@Preview
@Composable
fun MeloJJShapesPreview(
    @PreviewParameter(MeloJJShapesPreviewProvider::class) nameShape: Pair<String, Shape>,
) {
    Box(
        modifier = Modifier
            .size(64.dp)
            .border(1.dp, Color.White, nameShape.second)
            .clip(nameShape.second)
            .background(Color.Black),
        contentAlignment = Alignment.Center,
    ) {
        Text(text = nameShape.first, color = Color.White)
    }
}
