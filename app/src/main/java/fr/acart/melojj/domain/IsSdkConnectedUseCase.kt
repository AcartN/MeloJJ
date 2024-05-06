package fr.acart.melojj.domain

import fr.acart.melojj.data.SpotifySdkRepository
import kotlinx.coroutines.flow.StateFlow

fun interface IsSdkConnectedUseCase {
    operator fun invoke(): StateFlow<Boolean>

    companion object {
        fun init(
            spotifySdkRepository: SpotifySdkRepository,
        ): IsSdkConnectedUseCase = IsSdkConnectedUseCase { spotifySdkRepository.isConnected() }
    }
}