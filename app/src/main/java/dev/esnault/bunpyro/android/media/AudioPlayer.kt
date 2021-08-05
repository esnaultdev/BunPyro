package dev.esnault.bunpyro.android.media

import android.content.Context
import android.net.Uri
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.MediaSourceFactory
import com.google.android.exoplayer2.upstream.DataSpec
import com.google.android.exoplayer2.upstream.cache.*
import kotlinx.coroutines.*
import timber.log.Timber


private const val audioPrefix: String = "https://bunpro.jp/audio/"

class AudioPlayer(
    private val context: Context,
    private val mediaSourceFactory: MediaSourceFactory,
    private val cacheDataSource: CacheDataSource
) : IAudioPlayer {

    private val coroutineScope = CoroutineScope(SupervisorJob())

    override var listener: IAudioPlayer.Listener? = null

    // Lazy player that can be made null again
    private var _exoPlayer: ExoPlayer? = null
    private val exoPlayer: ExoPlayer
        get() = _exoPlayer ?: kotlin.run {
            SimpleExoPlayer.Builder(context)
                .build()
                .apply {
                    addListener(buildStateListener())
                    _exoPlayer = this
                }
        }

    override fun play(url: String?) {
        if (url == null) {
            stop()
            return
        }

        val uri = getAudioUri(url)
        val mediaItem = MediaItem.fromUri(uri)
        val source = mediaSourceFactory.createMediaSource(mediaItem)

        exoPlayer.setMediaSource(source)
        exoPlayer.prepare()
        exoPlayer.playWhenReady = true
    }

    override fun stop() {
        exoPlayer.stop()
    }

    override fun release() {
        _exoPlayer?.release()
        _exoPlayer = null
        coroutineScope.cancel()
    }

    override fun preload(url: String) {
        coroutineScope.launch(Dispatchers.IO) {
            val dataSpec = DataSpec(getAudioUri(url))
            val cacheWriter = CacheWriter(cacheDataSource, dataSpec, null, null)
            kotlin.runCatching { cacheWriter.cache() }
                .onFailure { Timber.w("Failed to preload $url") }
                .onSuccess { Timber.d("Preloaded $url") }
        }
    }

    private fun buildStateListener(): Player.Listener {
        return object : Player.Listener {
            override fun onPlayerStateChanged(
                playWhenReady: Boolean,
                playbackState: Int
            ) {
                val newState = when (playbackState) {
                    Player.STATE_IDLE -> AudioState.STOPPED
                    Player.STATE_BUFFERING -> AudioState.LOADING
                    Player.STATE_ENDED -> AudioState.STOPPED
                    Player.STATE_READY -> {
                        if (playWhenReady) {
                            AudioState.PLAYING
                        } else {
                            AudioState.PAUSED
                        }
                    }
                    else -> AudioState.STOPPED
                }
                listener?.onStateChange?.invoke(newState)
            }
        }
    }
}

fun getAudioUri(url: String): Uri {
    val baseUri = Uri.parse(url)
    return if (baseUri.isAbsolute) {
        baseUri
    } else {
        Uri.parse(audioPrefix + url)
    }
}
