package fr.acart.melojj.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

val LocalMeloJJSpacings = staticCompositionLocalOf<MeloJJSpacings> {
    error("No MeloJJSpacings provided")
}

@Immutable
data class MeloJJSpacings(
    val spacing02: Dp = 2.dp,
    val spacing04: Dp = 4.dp,
    val spacing08: Dp = 8.dp,
    val spacing12: Dp = 12.dp,
    val spacing16: Dp = 16.dp,
    val spacing24: Dp = 24.dp,
    val spacing32: Dp = 32.dp,
    val spacing40: Dp = 40.dp,
    val spacing48: Dp = 48.dp,
    val spacing64: Dp = 64.dp,
    val spacing80: Dp = 80.dp,
) {
    companion object {
        operator fun invoke() = MeloJJSpacings(
            spacing02 = 2.dp,
            spacing04 = 4.dp,
            spacing08 = 8.dp,
            spacing12 = 12.dp,
            spacing16 = 16.dp,
            spacing24 = 24.dp,
            spacing32 = 32.dp,
            spacing40 = 40.dp,
            spacing48 = 48.dp,
            spacing64 = 64.dp,
            spacing80 = 80.dp,
        )
    }
}