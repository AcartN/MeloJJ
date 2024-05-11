package fr.acart.melojj.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.haze
import dev.chrisbanes.haze.hazeChild
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
                        val hazeState = remember { HazeState() }
                        Box(
                            modifier = Modifier.fillMaxSize(),
                        ) {
                            Row(
                                modifier = Modifier
                                    .haze(hazeState),
                            ) {
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxHeight()
                                        .background(Color.Gray),
                                )
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxHeight()
                                        .background(Color.DarkGray),
                                )
                            }
                            Text(
                                modifier = Modifier
                                    .haze(hazeState)
                                    .align(Alignment.Center),
                                text = "ceci est un texte long",
                                color = Color.White,
                                fontSize = 24.sp,
                            )
                            Box(
                                modifier = Modifier
                                    .align(Alignment.Center)
                                    .hazeChild(
                                        state = hazeState,
                                        shape = MeloJJTheme.shapes.shapeCircle,
                                        style = HazeStyle(
                                            tint = Color.Black.copy(alpha = 0.05f),
                                            blurRadius = 2.dp,
                                            noiseFactor = 0.01f,
                                        ),
                                    )
                                    .size(164.dp),
                            ) {
                                Text(
                                    modifier = Modifier
                                        .padding(top = 16.dp)
                                        .align(Alignment.TopCenter),
                                    text = "Center",
                                    fontSize = 24.sp,
                                )
                            }
                        }
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