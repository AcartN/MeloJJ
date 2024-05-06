package fr.acart.melojj.ui.base

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import fr.acart.melojj.ui.theme.MeloJJTheme

fun Modifier.screenWithStatusBar() = composed {
    this
        .fillMaxSize()
        .background(MeloJJTheme.colors.background.secondary)
        .statusBarsPadding()
        .background(MeloJJTheme.colors.background.primary)
}