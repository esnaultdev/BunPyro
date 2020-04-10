package dev.esnault.bunpyro.android.media

import android.content.Context
import android.net.Uri
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util


private const val audioPrefix: String = "https://bunpro.jp/audio/"

class AudioPlayer(private val context: Context) : IAudioPlayer {

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

    private val mediaSourceFactory: ProgressiveMediaSource.Factory by lazy {
        val userAgent = Util.getUserAgent(context, "BunPyro")
        val dataSourceFactory = DefaultDataSourceFactory(context, userAgent)
        ProgressiveMediaSource.Factory(dataSourceFactory)
    }

    override fun play(url: String?) {
        if (url == null) {
            stop()
            return
        }

        val uri = Uri.parse(audioPrefix + url)
        val source = mediaSourceFactory.createMediaSource(uri)
        exoPlayer.prepare(source)
        exoPlayer.playWhenReady = true
    }

    override fun stop() {
        exoPlayer.stop()
    }

    override fun release() {
        _exoPlayer?.release()
        _exoPlayer = null
    }

    private fun buildStateListener(): Player.EventListener {
        return object : Player.EventListener {
            override fun onPlayerStateChanged(
                playWhenReady: Boolean,
                playbackState: Int
            ) {
                val newState = when (playbackState) {
                    Player.STATE_IDLE -> IAudioPlayer.State.STOPPED
                    Player.STATE_BUFFERING -> IAudioPlayer.State.LOADING
                    Player.STATE_ENDED -> IAudioPlayer.State.STOPPED
                    Player.STATE_READY -> {
                        if (playWhenReady) {
                            IAudioPlayer.State.PLAYING
                        } else {
                            IAudioPlayer.State.PAUSED
                        }
                    }
                    else -> IAudioPlayer.State.STOPPED
                }
                listener?.onStateChange?.invoke(newState)
            }
        }
    }
}
