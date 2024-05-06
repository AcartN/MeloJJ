package fr.acart.melojj.ui

import fr.acart.melojj.ui.home.HomeViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val uiModule = module {
    // Add your UI dependencies here
    viewModel { HomeViewModel() }
}