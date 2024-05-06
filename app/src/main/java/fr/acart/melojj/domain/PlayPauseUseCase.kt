package fr.acart.melojj.domain

import fr.acart.melojj.data.SpotifySdkRepository

fun interface PlayPauseUseCase {
    suspend operator fun invoke()

    companion object {
        fun init(
            spotifySdkRepository: SpotifySdkRepository,
        ): PlayPauseUseCase = PlayPauseUseCase { spotifySdkRepository.playPause() }
    }
}