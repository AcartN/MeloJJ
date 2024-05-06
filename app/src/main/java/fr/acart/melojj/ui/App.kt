package fr.acart.melojj.ui

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import fr.acart.melojj.ui.home.HomeScreen
import fr.acart.melojj.ui.theme.MeloJJTheme
import org.koin.androidx.compose.KoinAndroidContext
import org.koin.androidx.compose.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI

@OptIn(KoinExperimentalAPI::class)
@Composable
fun App() {
    KoinAndroidContext {
        MeloJJTheme {
            val navController = rememberNavController()
            NavHost(
                navController = navController,
                startDestination = HomeScreen.route,
            ) {
                composable(HomeScreen.route) {
                    HomeScreen(navHostController = navController, viewModel = koinViewModel())
                }
            }
        }
    }
}