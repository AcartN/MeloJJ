package fr.acart.melojj.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import fr.acart.melojj.R
import fr.acart.melojj.ui.base.UiScreen
import fr.acart.melojj.ui.base.screenWithStatusBar
import fr.acart.melojj.ui.theme.MeloJJTheme

object HomeScreen : UiScreen<HomeViewModel> {
    override val route: String = "home"

    @Composable
    override fun Content(navHostController: NavHostController, viewModel: HomeViewModel) {
        Box(
            modifier = Modifier.screenWithStatusBar(),
        ) {
            val uiState by viewModel.uiState.collectAsState()

            when (val state = uiState) {
                HomeState.Loading -> Box(contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = MeloJJTheme.colors.content.primary)
                }

                is HomeState.Loaded -> {
                    Column {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(MeloJJTheme.shapes.shapeCircle)
                                .background(if (state.isConnectedToSdk) Color.Green else Color.Red)
                        )
                        Text(text = "Home")
                        Button(
                            onClick = { viewModel.triggerAction(HomeAction.PlayPause) },
                        ) {
                            Text(text = "Play/Pause")
                        }
                        Icon(
                            modifier = Modifier
                                .padding(16.dp)
                                .size(64.dp),
                            painter = painterResource(id = R.drawable.music_note),
                            contentDescription = null,
                            tint = MeloJJTheme.colors.content.primary
                        )
                    }
                }

                HomeState.Error -> {
                    Text(text = "Error")
                }
            }

            LaunchedEffect(Unit) {
                viewModel.uiEffects.collect {
                    when (it) {
                        HomeEffect.NavigateToSettings -> TODO()
                    }
                }
            }
        }
    }
}