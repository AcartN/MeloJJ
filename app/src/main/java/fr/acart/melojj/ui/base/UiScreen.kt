package fr.acart.melojj.ui.base

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.navigation.NavHostController

interface UiScreen<VM : ViewModel> {

    val route: String

    @Composable
    fun Content(navHostController: NavHostController, viewModel: VM)

    @Composable
    operator fun invoke(navHostController: NavHostController, viewModel: VM) {
        Content(navHostController, viewModel)
    }

    infix fun <T> T.with(other: T): Pair<T, T> = Pair(this, other)

    fun <T> String.replace(
        vararg replacements: Pair<String, T>,
    ): String = replacements.fold(this) { acc, (key, value) ->
        acc.replace("{$key}", value.toString())
    }

}
