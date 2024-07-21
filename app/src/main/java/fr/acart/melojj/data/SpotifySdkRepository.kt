package fr.acart.melojj.data

import android.graphics.Bitmap
import android.util.Log
import arrow.core.Either
import arrow.core.left
import arrow.core.raise.either
import arrow.core.right
import com.spotify.android.appremote.api.ContentApi.ContentType
import com.spotify.android.appremote.api.SpotifyAppRemote
import com.spotify.protocol.client.CallResult
import com.spotify.protocol.client.Subscription
import com.spotify.protocol.types.CrossfadeState
import com.spotify.protocol.types.Empty
import com.spotify.protocol.types.Image
import com.spotify.protocol.types.ImageUri
import com.spotify.protocol.types.LibraryState
import com.spotify.protocol.types.ListItem
import com.spotify.protocol.types.ListItems
import com.spotify.protocol.types.PlayerContext
import com.spotify.protocol.types.PlayerState
import com.spotify.protocol.types.VolumeState
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.map
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class SpotifySdkRepository(private val spotifyAppRemoteProvider: SpotifyAppRemoteProvider) {

    fun isConnected(): StateFlow<Boolean> = spotifyAppRemoteProvider.connectionState

    private suspend fun remote() = spotifyAppRemoteProvider.provideSpotifyAppRemote()

    private val playerStatusFlow: Flow<PlayerStatus> by lazy {
        subscriptionFlow { playerApi.subscribeToPlayerState() }.map { it.toPlayerStatus() }
    }

    fun playerStatusFlow(): Flow<PlayerStatus> = playerStatusFlow
    suspend fun playerStatusActualState(): Either<Throwable, PlayerStatus> = either {
        remote()
            .playerApi
            .playerState
            ?.suspendUntilResult()
            ?.bind()
            ?.toPlayerStatus()
            ?: raise(IllegalStateException("Player state is null"))
    }

    suspend fun playPause(): Either<Throwable, Unit> = either {
        executeCall {
            when {
                playerStatusActualState().bind().isPaused -> playerApi.resume()
                else -> playerApi.pause()
            }
        }
    }

    suspend fun resume(): Either<Throwable, Unit> = executeCall { playerApi.resume() }
    suspend fun pause(): Either<Throwable, Unit> = executeCall { playerApi.pause() }
    suspend fun skipNext(): Either<Throwable, Unit> = executeCall { playerApi.skipNext() }
    suspend fun skipPrevious(): Either<Throwable, Unit> = executeCall { playerApi.skipPrevious() }
    suspend fun seekTo(position: Long): Either<Throwable, Unit> =
        executeCall { playerApi.seekTo(position) }

    suspend fun setVolume(volume: Float): Either<Throwable, Unit> =
        executeCall { connectApi.connectSetVolume(volume) }

    fun volumeFlow(): Flow<Volume> =
        subscriptionFlow { connectApi.subscribeToVolumeState() }.map { it.toVolume() }

    suspend fun setShuffle(isShuffling: Boolean): Either<Throwable, Unit> =
        executeCall { playerApi.setShuffle(isShuffling) }

    suspend fun setRepeatMode(repeatMode: RepeatMode): Either<Throwable, Unit> =
        executeCall { playerApi.setRepeat(repeatMode.value) }

    suspend fun toggleShuffle(): Either<Throwable, Unit> = executeCall { playerApi.toggleShuffle() }
    suspend fun toggleRepeatMode(): Either<Throwable, Unit> =
        executeCall { playerApi.toggleRepeat() }

    suspend fun playUri(uri: String): Either<Throwable, Unit> = executeCall { playerApi.play(uri) }
    suspend fun playIndexOfPlaylist(playlistUri: String, index: Int): Either<Throwable, Unit> =
        executeCall { playerApi.skipToIndex(playlistUri, index) }

    suspend fun playIndexOfAlbum(albumUri: String, index: Int): Either<Throwable, Unit> =
        executeCall { playerApi.skipToIndex(albumUri, index) }

    suspend fun queueUri(uri: String): Either<Throwable, Unit> =
        executeCall { playerApi.queue(uri) }

    suspend fun crossfadeState(): Either<Throwable, Crossfade> =
        remote().playerApi.crossfadeState.suspendUntilResult().map { it.toCrossfade() }

    fun playerContextFlow(): Flow<PlayingFrom> =
        subscriptionFlow { playerApi.subscribeToPlayerContext() }.map { it.toPlayingFrom() }

    suspend fun switchToLocalDevice(): Either<Throwable, Unit> =
        executeCall { connectApi.connectSwitchToLocalDevice() }

    suspend fun decreaseVolume(): Either<Throwable, Unit> =
        executeCall { connectApi.connectDecreaseVolume() }

    suspend fun increaseVolume(): Either<Throwable, Unit> =
        executeCall { connectApi.connectIncreaseVolume() }

    suspend fun addToLibrary(uri: String): Either<Throwable, Unit> =
        executeCall { userApi.addToLibrary(uri) }

    suspend fun removeFromLibrary(uri: String): Either<Throwable, Unit> =
        executeCall { userApi.removeFromLibrary(uri) }

    suspend fun getLibraryStatus(
        uri: String,
    ): Either<Throwable, LibraryStatus> = executeCallWithResult<LibraryState> {
        userApi.getLibraryState(uri)
    }.map { it.toLibraryStatus() }

    suspend fun playContentItem(contentItem: ContentItem) {
        if (contentItem is PlayableContentItemImpl)
            executeCall { contentApi.playContentItem(contentItem.listItem) }
        else
            Log.e("SpotifySdkRepository", "Unsupported content item type: $contentItem")
    }

    suspend fun getRecommendedContent(type: ContentType): List<ContentItem> =
        executeCallWithResult<ListItems> { contentApi.getRecommendedContentItems(type.toString()) }
            .fold(
                ifLeft = {
                    Log.e("SpotifySdkRepository", "Failed to get recommended content: $it")
                    emptyList()
                },
                ifRight = { it.items.asList().toContentItems() },
            )

    suspend fun getImage(imageUri: String): Either<Throwable, Bitmap> =
        executeCallWithResult<Bitmap> {
            imagesApi.getImage(ImageUri(imageUri))
        }

    suspend fun getImage(
        imageUri: String,
        imageDimension: ImageDimension,
    ): Either<Throwable, Bitmap> =
        executeCallWithResult<Bitmap> {
            imagesApi.getImage(ImageUri(imageUri), imageDimension.toImageDimension())
        }

    private suspend fun List<ListItem>.toContentItems(): List<ContentItem> = mapNotNull { item ->
        when {
            item.playable -> PlayableContentItemImpl(
                id = item.id,
                uri = item.uri,
                imageUri = item.imageUri.raw,
                title = item.title,
                subtitle = item.subtitle,
                listItem = item
            )

            item.hasChildren -> ChildrenContentItemImpl(
                id = item.id,
                uri = item.uri,
                imageUri = item.imageUri.raw,
                title = item.title,
                subtitle = item.subtitle,
                children = { perPage, offset ->
                    remote()
                        .contentApi
                        .getChildrenOfItem(item, perPage, offset)
                        .suspendUntilResult()
                        .fold(
                            ifLeft = {
                                Log.e("SpotifySdkRepository", "Failed to get children: $it")
                                emptyList()
                            },
                            ifRight = { it.items.asList().toContentItems() },
                        )
                },
                listItem = item,
            )

            else -> {
                Log.e("SpotifySdkRepository", "Unsupported item type: $item")
                null
            }
        }
    }


    // Helper functions
    private suspend fun CallResult<Empty>.suspendUntilDone(): Either<Throwable, Unit> = try {
        suspendCoroutine { continuation -> setResultCallback { continuation.resume(Unit.right()) } }
    } catch (throwable: Throwable) {
        throwable.left()
    }

    private suspend fun <T> CallResult<T>.suspendUntilResult(): Either<Throwable, T> = try {
        suspendCoroutine { continuation -> setResultCallback { continuation.resume(it.right()) } }
    } catch (throwable: Throwable) {
        throwable.left()
    }

    private suspend fun <T> Subscription<T>.asFlow(): Flow<T> = callbackFlow {
        this@asFlow.setEventCallback { trySendBlocking(it) }
        awaitClose { this@asFlow.cancel() }
    }

    private fun <T> subscriptionFlow(
        getSubscription: suspend SpotifyAppRemote.() -> Subscription<T>,
    ): Flow<T> = callbackFlow {
        val subscription = getSubscription(remote())
        subscription.setEventCallback { trySendBlocking(it) }
        awaitClose { subscription.cancel() }
    }

    private suspend fun executeCall(
        call: suspend SpotifyAppRemote.() -> CallResult<Empty>,
    ): Either<Throwable, Unit> = call(remote()).suspendUntilDone()

    private suspend fun <T> executeCallWithResult(
        call: suspend SpotifyAppRemote.() -> CallResult<T>,
    ) = call(remote()).suspendUntilResult()

}

private fun CrossfadeState.toCrossfade(): Crossfade = when {
    isEnabled -> Crossfade.On(duration)
    else -> Crossfade.Off
}

sealed interface Crossfade {
    data object Off : Crossfade
    data class On(val duration: Int) : Crossfade
}

enum class ImageDimension {
    SMALL,
    MEDIUM,
    LARGE,
    THUMBNAIL,
    X_SMALL,
}

private fun ImageDimension.toImageDimension(): Image.Dimension = when (this) {
    ImageDimension.SMALL -> Image.Dimension.SMALL
    ImageDimension.MEDIUM -> Image.Dimension.MEDIUM
    ImageDimension.LARGE -> Image.Dimension.LARGE
    ImageDimension.THUMBNAIL -> Image.Dimension.THUMBNAIL
    ImageDimension.X_SMALL -> Image.Dimension.X_SMALL
}

sealed interface ContentItem

sealed interface PlayableContentItem : ContentItem {
    val id: String
    val uri: String
    val imageUri: String?
    val title: String
    val subtitle: String
}

private data class PlayableContentItemImpl(
    override val id: String,
    override val uri: String,
    override val imageUri: String?,
    override val title: String,
    override val subtitle: String,
    val listItem: ListItem,
) : PlayableContentItem

sealed interface ChildrenContentItem : ContentItem {
    val id: String
    val uri: String
    val imageUri: String?
    val title: String
    val subtitle: String
    val children: suspend (perPage: Int, offset: Int) -> List<ContentItem>
}

private data class ChildrenContentItemImpl(
    override val id: String,
    override val uri: String,
    override val imageUri: String?,
    override val title: String,
    override val subtitle: String,
    override val children: suspend (perPage: Int, offset: Int) -> List<ContentItem>,
    val listItem: ListItem,
) : ChildrenContentItem

private fun PlayerContext.toPlayingFrom(): PlayingFrom = PlayingFrom(
    uri = uri,
    title = title,
    subtitle = subtitle,
    type = ContextType
        .entries
        .find { type.equals(it.value, ignoreCase = true) }
        ?: ContextType.UNKNOWN
            .also { Log.e("SpotifySdkRepository", "Unknown context type: $type") },
)

data class PlayingFrom(
    val uri: String,
    val title: String,
    val subtitle: String,
    val type: ContextType,
)

enum class ContextType(val value: String) {
    ARTIST("artist"),
    PLAYLIST("playlist"),
    ALBUM("album"),
    SHOW("show"),
    UNKNOWN("unknown"),
}

data class PlayerStatus(
    val trackName: String,
    val artist: Artist,
    val album: Album,
    val isPaused: Boolean,
    // The position in milliseconds
    val position: Long,
    // The duration of the track in milliseconds
    val duration: Long,
    val repeatMode: RepeatMode,
    val isShuffling: Boolean,
    val restrictions: PlaybackRestrictions,
)

enum class RepeatMode(val value: Int) {
    OFF(0),
    ONE(1),
    ALL(2),
}

data class PlaybackRestrictions(
    val canSkipNext: Boolean,
    val canSkipPrevious: Boolean,
    val canSeek: Boolean,
    val canRepeatTrack: Boolean,
    val canRepeatContext: Boolean,
    val canToggleShuffle: Boolean,
)

data class LibraryStatus(
    val uri: String,
    val isAdded: Boolean,
    val canAdd: Boolean,
)

private fun LibraryState.toLibraryStatus(): LibraryStatus = LibraryStatus(uri, isAdded, canAdd)

data class Artist(
    val name: String,
    val uri: String,
)

data class Album(
    val name: String,
    val uri: String,
)

data class Volume(
    val volume: Float,
    val isControllable: Boolean,
)

private fun VolumeState.toVolume(): Volume = Volume(mVolume, mControllable)

private fun PlayerState.toPlayerStatus(): PlayerStatus {
    return PlayerStatus(
        trackName = track.name,
        artist = Artist(
            name = track.artist.name,
            uri = track.artist.uri,
        ),
        album = Album(
            name = track.album.name,
            uri = track.album.uri,
        ),
        isPaused = isPaused,
        position = playbackPosition,
        duration = track.duration,
        repeatMode = RepeatMode.entries[playbackOptions.repeatMode],
        isShuffling = playbackOptions.isShuffling,
        restrictions = PlaybackRestrictions(
            canSkipNext = playbackRestrictions.canSkipNext,
            canSkipPrevious = playbackRestrictions.canSkipPrev,
            canSeek = playbackRestrictions.canSeek,
            canRepeatTrack = playbackRestrictions.canRepeatTrack,
            canRepeatContext = playbackRestrictions.canRepeatContext,
            canToggleShuffle = playbackRestrictions.canToggleShuffle,
        ),
    )
}