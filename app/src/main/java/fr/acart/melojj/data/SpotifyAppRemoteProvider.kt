package fr.acart.melojj.data

import android.app.Application
import com.spotify.android.appremote.api.ConnectionParams
import com.spotify.android.appremote.api.Connector
import com.spotify.android.appremote.api.SpotifyAppRemote
import fr.acart.melojj.BuildConfig
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class SpotifyAppRemoteProvider(private val application: Application) {

    private var spotifyAppRemote: SpotifyAppRemote? = null

    private val clientId = BuildConfig.SPOTIFY_CLIENT_ID
    private val redirectUri = BuildConfig.SPOTIFY_REDIRECT_URI

    private val mutableConnectionState = MutableStateFlow(false)
    val connectionState = mutableConnectionState.asStateFlow()

    // Set the connection parameters
    private val connectionParams = ConnectionParams.Builder(clientId)
        .setRedirectUri(redirectUri)
        .showAuthView(true)
        .build()

    suspend fun provideSpotifyAppRemote(): SpotifyAppRemote {
        spotifyAppRemote?.let { appRemote ->
            if (appRemote.isConnected) return appRemote
        }
        return suspendCoroutine { continuation ->
            if (SpotifyAppRemote.isSpotifyInstalled(application)) {
                SpotifyAppRemote.connect(
                    application,
                    connectionParams,
                    object : Connector.ConnectionListener {
                        override fun onConnected(appRemote: SpotifyAppRemote) {
                            spotifyAppRemote = appRemote
                            continuation.resume(appRemote)
                        }

                        override fun onFailure(throwable: Throwable) {
                            spotifyAppRemote = null
                            continuation.resumeWith(Result.failure(throwable))
                        }
                    }
                )
            } else {
                spotifyAppRemote = null
                continuation.resumeWith(Result.failure(SpotifyNotInstalledException()))
            }
        }.also { mutableConnectionState.value = spotifyAppRemote?.isConnected == true }
    }

    fun close() {
        SpotifyAppRemote.disconnect(spotifyAppRemote)
        spotifyAppRemote = null
        mutableConnectionState.value = false
    }

    class SpotifyNotInstalledException : Exception("Spotify is not installed on the device")

}