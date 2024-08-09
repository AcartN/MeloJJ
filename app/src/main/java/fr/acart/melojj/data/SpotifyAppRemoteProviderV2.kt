package fr.acart.melojj.data

import android.app.Application
import android.util.Log
import arrow.core.Either
import arrow.core.raise.Raise
import arrow.core.raise.either
import com.spotify.android.appremote.api.ConnectionParams
import com.spotify.android.appremote.api.Connector
import com.spotify.android.appremote.api.SpotifyAppRemote
import fr.acart.melojj.BuildConfig
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.coroutines.resume
import kotlin.time.Duration.Companion.seconds

class SpotifyAppRemoteProviderV2(private val application: Application) : AutoCloseable {

    private var spotifyAppRemote: SpotifyAppRemote? = null

    private val mutableConnectionErrorEvents = MutableSharedFlow<SpotifyRemoteError.Connection>(
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )
    val connectionErrorEvents = mutableConnectionErrorEvents.asSharedFlow()

    suspend fun appRemote(): Either<SpotifyRemoteError.Connection, SpotifyAppRemote> = either {
        val appRemote = spotifyAppRemote
        when {
            appRemote != null && appRemote.isConnected -> appRemote
            else -> connectToSpotifySDK()
        }
    }

    private suspend fun Raise<SpotifyRemoteError.Connection>.connectToSpotifySDK() =
        withTimeoutOrNull<SpotifyAppRemote>(ConnectionTimeout) {
            suspendCancellableCoroutine { continuation ->
                if (SpotifyAppRemote.isSpotifyInstalled(application)) {
                    SpotifyAppRemote.connect(
                        application,
                        SpotifyConnectionParams,
                        object : Connector.ConnectionListener {
                            override fun onConnected(appRemote: SpotifyAppRemote) {
                                spotifyAppRemote = appRemote
                                if (continuation.isActive)
                                    continuation.resume(appRemote)
                                else
                                    Log.d(
                                        "SpotifyRepository",
                                        "Connected but continuation is not active",
                                    )
                            }

                            override fun onFailure(throwable: Throwable) {
                                Log.e(
                                    "SpotifyRepository",
                                    "Spotify connection failed",
                                    throwable,
                                )
                                val error = SpotifyRemoteError.Connection.Failure(throwable)
                                mutableConnectionErrorEvents.tryEmit(error)
                                close()
                                raise(error)
                            }
                        }
                    )
                } else raise(SpotifyRemoteError.Connection.NotInstalled)
            }
        } ?: raise(SpotifyRemoteError.Connection.Timeout)

    override fun close() {
        SpotifyAppRemote.disconnect(spotifyAppRemote)
        spotifyAppRemote = null
    }

    companion object {
        private const val ClientId = BuildConfig.SPOTIFY_CLIENT_ID
        private const val RedirectUri = BuildConfig.SPOTIFY_REDIRECT_URI

        private val ConnectionTimeout = 3.seconds

        // Set the connection parameters
        private val SpotifyConnectionParams = ConnectionParams.Builder(ClientId)
            .setRedirectUri(RedirectUri)
            .showAuthView(true)
            .build()
    }

}