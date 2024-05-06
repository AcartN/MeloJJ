package fr.acart.melojj.ui.home

import androidx.lifecycle.viewModelScope
import fr.acart.melojj.ui.base.BaseViewModel
import fr.acart.melojj.ui.base.UiAction
import fr.acart.melojj.ui.base.UiEffect
import fr.acart.melojj.ui.base.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HomeViewModel : BaseViewModel<HomeState, HomeAction, HomeEffect>() {

    override val uiState: StateFlow<HomeState> = MutableStateFlow(HomeState.Loading)

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
