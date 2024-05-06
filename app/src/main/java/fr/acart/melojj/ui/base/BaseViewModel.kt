package fr.acart.melojj.ui.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

abstract class BaseViewModel<UI_STATE : UiState, UI_ACTION : UiAction, UI_EFFECT : UiEffect> :
    ViewModel(), HasUiState<UI_STATE>, HasUiAction<UI_ACTION>, HasUiEffect<UI_EFFECT> {
    protected abstract suspend fun actionReducer(action: UI_ACTION)

    private val mutex = Mutex()
    override fun triggerAction(action: UI_ACTION) {
        viewModelScope.launch {
            mutex.withLock {
                actionReducer(action)
            }
        }
    }

    private val mutableEffects = MutableSharedFlow<UI_EFFECT>()
    override val uiEffects: Flow<UI_EFFECT> = mutableEffects.asSharedFlow()
    protected fun triggerEffect(effect: UI_EFFECT) {
        viewModelScope.launch {
            mutableEffects.emit(effect)
        }
    }

}


interface UiState
interface HasUiState<UI_STATE : UiState> {
    val uiState: StateFlow<UI_STATE>
}

interface UiAction
interface HasUiAction<UI_ACTION : UiAction> {
    fun triggerAction(action: UI_ACTION)
}

interface UiEffect
interface HasUiEffect<UI_EFFECT : UiEffect> {
    val uiEffects: Flow<UI_EFFECT>
}