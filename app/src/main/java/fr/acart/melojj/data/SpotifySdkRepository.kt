package fr.acart.melojj.data

import kotlinx.coroutines.flow.StateFlow
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class SpotifySdkRepository(private val spotifyAppRemoteProvider: SpotifyAppRemoteProvider) {

    fun isConnected(): StateFlow<Boolean> = spotifyAppRemoteProvider.connectionState
    suspend fun playPause() {
        val remote = spotifyAppRemoteProvider.provideSpotifyAppRemote()
        suspendCoroutine { continuation ->
            remote
                .playerApi
                .playerState
                ?.setResultCallback { playerState ->
                    when {
                        playerState.isPaused -> remote.playerApi.resume()
                        else -> remote.playerApi.pause()
                    }.setResultCallback {
                        continuation.resume(Unit)
                    }
                }
        }
    }

}