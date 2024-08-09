package fr.acart.melojj.data

import android.graphics.Bitmap
import arrow.core.Either
import arrow.core.raise.Raise
import arrow.core.raise.either
import com.spotify.android.appremote.api.SpotifyAppRemote
import com.spotify.protocol.client.CallResult
import com.spotify.protocol.client.Subscription
import com.spotify.protocol.types.Empty
import com.spotify.protocol.types.ImageUri
import com.spotify.protocol.types.PlayerState
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.map
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class SpotifySdkRepositoryV2(
    private val spotifyAppRemoteProvider: SpotifyAppRemoteProviderV2,
) {

    private suspend fun Raise<SpotifyRemoteError.Connection>.remote() =
        spotifyAppRemoteProvider.appRemote().bind()

    /** API */
    fun playerStatusFlow(): Either<SpotifyRemoteError.Connection, Flow<PlayerStatus>> =
        subscriptionFlow {
            playerApi.subscribeToPlayerState()
        }.map { playerStateFlow ->
            playerStateFlow.map { it.toPlayerStatus() }
        }

    suspend fun actualPlayerStatus(): Either<SpotifyRemoteError, PlayerStatus> =
        eitherQuery<PlayerState?> { remote ->
            remote.playerApi.playerState
        }.map { it.toPlayerStatus() }

    private suspend fun Raise<SpotifyRemoteError>.playerStatus(): PlayerStatus =
        actualPlayerStatus().bind()

    fun connectionErrorEvents(): SharedFlow<SpotifyRemoteError.Connection> =
        spotifyAppRemoteProvider.connectionErrorEvents

    suspend fun playPause(): Either<SpotifyRemoteError, Unit> =
        eitherCommand { remote ->
            when (val playerStatus = playerStatus()) {
                PlayerStatus.NoStatus -> remote.playerApi.resume()
                is PlayerStatus.Active -> when (playerStatus.isPaused) {
                    true -> remote.playerApi.resume()
                    false -> remote.playerApi.pause()
                }
            }
        }

    suspend fun resume(): Either<SpotifyRemoteError, Unit> =
        eitherCommand { remote -> remote.playerApi.resume() }

    suspend fun pause(): Either<SpotifyRemoteError, Unit> =
        eitherCommand { remote -> remote.playerApi.pause() }

    suspend fun skipNext(): Either<SpotifyRemoteError, Unit> =
        eitherCommand { remote ->
            remote.playerApi.skipNext()
        }

    suspend fun skipPrevious(): Either<SpotifyRemoteError, Unit> =
        eitherCommand { remote ->
            remote.playerApi.skipPrevious()
        }

    suspend fun seekTo(position: Long): Either<SpotifyRemoteError, Unit> =
        eitherCommand { remote ->
            remote.playerApi.seekTo(position)
        }

    suspend fun setVolume(volume: Float): Either<SpotifyRemoteError, Unit> =
        eitherCommand { remote ->
            remote.connectApi.connectSetVolume(volume)
        }

    fun volumeFlow(): Either<SpotifyRemoteError, Flow<Volume>> =
        subscriptionFlow {
            connectApi.subscribeToVolumeState()
        }.map { volumeFlow ->
            volumeFlow.map { it.toVolume() }
        }

    suspend fun setShuffle(shuffle: Boolean): Either<SpotifyRemoteError, Unit> =
        eitherCommand { remote ->
            remote.playerApi.setShuffle(shuffle)
        }

    suspend fun setRepeatMode(repeatMode: RepeatMode): Either<SpotifyRemoteError, Unit> =
        eitherCommand { remote ->
            remote.playerApi.setRepeat(repeatMode.value)
        }

    suspend fun toggleShuffle(): Either<SpotifyRemoteError, Unit> =
        eitherCommand { remote ->
            remote.playerApi.toggleShuffle()
        }

    suspend fun toggleRepeatMode(): Either<SpotifyRemoteError, Unit> =
        eitherCommand { remote ->
            remote.playerApi.toggleRepeat()
        }

    suspend fun playUri(uri: String): Either<SpotifyRemoteError, Unit> =
        eitherCommand { remote ->
            remote.playerApi.play(uri)
        }

    suspend fun playIndexOfPlaylist(
        playlistUri: String,
        index: Int,
    ): Either<SpotifyRemoteError, Unit> =
        eitherCommand { remote ->
            remote.playerApi.skipToIndex(playlistUri, index)
        }

    suspend fun playIndexOfAlbum(
        albumUri: String,
        index: Int,
    ): Either<SpotifyRemoteError, Unit> =
        eitherCommand { remote ->
            remote.playerApi.skipToIndex(albumUri, index)
        }

    suspend fun queueUri(uri: String): Either<SpotifyRemoteError, Unit> =
        eitherCommand { remote ->
            remote.playerApi.queue(uri)
        }

    suspend fun crossfadeState(): Either<SpotifyRemoteError, Crossfade> =
        eitherQuery { remote ->
            remote.playerApi.crossfadeState
        }.map { it.toCrossfade() }

    fun playerContextFlow(): Either<SpotifyRemoteError, Flow<PlayingFrom>> =
        subscriptionFlow {
            playerApi.subscribeToPlayerContext()
        }.map { playerContextFlow ->
            playerContextFlow.map { it.toPlayingFrom() }
        }

    suspend fun switchToLocalDevice(): Either<SpotifyRemoteError, Unit> =
        eitherCommand { remote ->
            remote.connectApi.connectSwitchToLocalDevice()
        }

    suspend fun decreaseVolume(): Either<SpotifyRemoteError, Unit> =
        eitherCommand { remote ->
            remote.connectApi.connectDecreaseVolume()
        }

    suspend fun increaseVolume(): Either<SpotifyRemoteError, Unit> =
        eitherCommand { remote ->
            remote.connectApi.connectIncreaseVolume()
        }

    suspend fun addToLibrary(uri: String): Either<SpotifyRemoteError, Unit> =
        eitherCommand { remote ->
            remote.userApi.addToLibrary(uri)
        }

    suspend fun removeFromLibrary(uri: String): Either<SpotifyRemoteError, Unit> =
        eitherCommand { remote ->
            remote.userApi.removeFromLibrary(uri)
        }

    suspend fun getLibraryStatus(uri: String): Either<SpotifyRemoteError, LibraryStatus> =
        eitherQuery { remote ->
            remote.userApi.getLibraryState(uri)
        }.map { it.toLibraryStatus() }

    suspend fun playContentItem(contentItem: ContentItem): Either<SpotifyRemoteError, Unit> =
        eitherCommand { remote ->
            if (contentItem is PlayableContentItemImpl)
                remote.playerApi.play(contentItem.uri)
            else
                raise(SpotifyRemoteError.UnsupportedContentItemType(contentItem))
        }

    suspend fun getImage(imageUri: String): Either<SpotifyRemoteError, Bitmap> =
        eitherQuery { remote ->
            remote.imagesApi.getImage(ImageUri(imageUri))
        }

    suspend fun getImage(
        imageUri: String,
        imageDimension: ImageDimension,
    ): Either<SpotifyRemoteError, Bitmap> =
        eitherQuery { remote ->
            remote.imagesApi.getImage(ImageUri(imageUri), imageDimension.toImageDimension())
        }


    /** Helpers **/
    private suspend fun eitherCommand(
        call: suspend Raise<SpotifyRemoteError>.(remote: SpotifyAppRemote) -> CallResult<Empty>,
    ): Either<SpotifyRemoteError, Unit> = either {
        command { call(remote()) }
    }

    private suspend fun <T> eitherQuery(
        call: suspend Raise<SpotifyRemoteError>.(remote: SpotifyAppRemote) -> CallResult<T>,
    ): Either<SpotifyRemoteError, T> = either {
        query { call(remote()) }
    }

    private suspend fun Raise<SpotifyRemoteError>.command(
        call: suspend SpotifyAppRemote.() -> CallResult<Empty>,
    ): Unit = call(remote()).suspendUntilDone().bind()

    private suspend fun <T> Raise<SpotifyRemoteError>.query(
        call: suspend SpotifyAppRemote.() -> CallResult<T>,
    ): T = call(remote()).suspendUntilResult().bind()

    private suspend fun CallResult<Empty>.suspendUntilDone(): Either<SpotifyRemoteError, Unit> =
        either {
            try {
                suspendCoroutine { continuation -> setResultCallback { continuation.resume(Unit) } }
            } catch (throwable: Throwable) {
                raise(SpotifyRemoteError.CallError.Failure(throwable))
            }
        }

    private suspend fun <T> CallResult<T>.suspendUntilResult(): Either<SpotifyRemoteError, T> =
        either {
            try {
                suspendCoroutine { continuation -> setResultCallback { continuation.resume(it) } }
            } catch (throwable: Throwable) {
                raise(SpotifyRemoteError.CallError.Failure(throwable))
            }
        }

    private fun <T> subscriptionFlow(
        getSubscription: suspend SpotifyAppRemote.() -> Subscription<T>,
    ): Either<SpotifyRemoteError.Connection, Flow<T>> = either {
        callbackFlow {
            val subscription = getSubscription(remote())
            subscription.setEventCallback { trySendBlocking(it) }
            awaitClose { subscription.cancel() }
        }
    }

}
