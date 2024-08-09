package fr.acart.melojj.data

sealed class SpotifyRemoteError(
    open val message: String,
) {
    sealed class Connection(override val message: String) : SpotifyRemoteError(message) {
        data object NotInstalled : Connection("Spotify is not installed on the device")
        data object Timeout : Connection("Spotify connection timed out")
        class Failure(
            val throwable: Throwable,
        ) : Connection("Spotify connection failed : ${throwable.message}")
    }

    sealed class CallError(override val message: String) : SpotifyRemoteError(message) {
        class Failure(
            val throwable: Throwable,
        ) : CallError("Spotify call failed : ${throwable.message}")
    }

    data class UnsupportedContentItemType(
        val contentItem: ContentItem,
    ) : SpotifyRemoteError("Unsupported content item type : $contentItem")


}