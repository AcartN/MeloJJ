package fr.acart.melojj.ui.home

import androidx.lifecycle.viewModelScope
import fr.acart.melojj.ui.base.BaseViewModel
import fr.acart.melojj.ui.base.UiAction
import fr.acart.melojj.ui.base.UiEffect
import fr.acart.melojj.ui.base.UiState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds

class HomeViewModel : BaseViewModel<HomeState, HomeAction, HomeEffect>() {

    private val mutableUiState = MutableStateFlow<HomeState>(HomeState.Loading)
    override val uiState = mutableUiState.asStateFlow()

    init {
        viewModelScope.launch {
            delay(5.seconds)
            mutableUiState.value = HomeState.Loaded
        }
    }

    override suspend fun actionReducer(action: HomeAction) {
        when (action) {
            HomeAction.PlayPause -> playPause()
            HomeAction.NavigateToSettings -> navigateToSettings()
        }
    }

    private fun playPause() {
        viewModelScope.launch {
            TODO()
        }
    }

    private fun navigateToSettings() {
        triggerEffect(HomeEffect.NavigateToSettings)
    }

}


sealed interface HomeState : UiState {
    data object Loading : HomeState
    data object Loaded : HomeState
    data object Error : HomeState
}

sealed interface HomeAction : UiAction {
    data object PlayPause : HomeAction
    data object NavigateToSettings : HomeAction
}

sealed interface HomeEffect : UiEffect {
    data object NavigateToSettings : HomeEffect
}
