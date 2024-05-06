package fr.acart.melojj.ui.home

import androidx.lifecycle.viewModelScope
import fr.acart.melojj.domain.IsSdkConnectedUseCase
import fr.acart.melojj.domain.PlayPauseUseCase
import fr.acart.melojj.ui.base.BaseViewModel
import fr.acart.melojj.ui.base.UiAction
import fr.acart.melojj.ui.base.UiEffect
import fr.acart.melojj.ui.base.UiState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds

class HomeViewModel(
    isSdkConnectedUseCase: IsSdkConnectedUseCase,
    private val playPauseUseCase: PlayPauseUseCase,
) : BaseViewModel<HomeState, HomeAction, HomeEffect>() {

    override val uiState = isSdkConnectedUseCase()
        .map { isConnected -> HomeState.Loaded(isConnected) }
        .onStart { delay(5.seconds) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = HomeState.Loading,
        )

    override suspend fun actionReducer(action: HomeAction) {
        when (action) {
            HomeAction.PlayPause -> playPause()
            HomeAction.NavigateToSettings -> navigateToSettings()
        }
    }

    private fun playPause() {
        viewModelScope.launch {
            playPauseUseCase()
        }
    }

    private fun navigateToSettings() {
        triggerEffect(HomeEffect.NavigateToSettings)
    }

}


sealed interface HomeState : UiState {
    data object Loading : HomeState
    data class Loaded(
        val isConnectedToSdk: Boolean,
    ) : HomeState

    data object Error : HomeState
}

sealed interface HomeAction : UiAction {
    data object PlayPause : HomeAction
    data object NavigateToSettings : HomeAction
}

sealed interface HomeEffect : UiEffect {
    data object NavigateToSettings : HomeEffect
}
