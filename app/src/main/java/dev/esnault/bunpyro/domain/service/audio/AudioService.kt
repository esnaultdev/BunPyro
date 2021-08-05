package dev.esnault.bunpyro.domain.service.audio

import dev.esnault.bunpyro.android.media.AudioState
import dev.esnault.bunpyro.android.media.IAudioPlayer
import dev.esnault.bunpyro.domain.entities.media.AudioItem
import dev.esnault.bunpyro.domain.entities.media.CurrentAudio
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow


class AudioService(private val audioPlayer: IAudioPlayer) : IAudioService {

    private var _currentAudio: CurrentAudio?
        get() = currentAudioFlow.value
        set(value) {
            currentAudioFlow.tryEmit(value)
        }
    private var currentAudioFlow = MutableStateFlow<CurrentAudio?>(null)
    override val currentAudio: Flow<CurrentAudio?> = currentAudioFlow

    override fun playOrStop(audioItem: AudioItem) {
        val currentAudio = _currentAudio

        when {
            currentAudio == null -> { // Not playing anything yet
                audioPlayer.listener = buildAudioListener()
                audioPlayer.play(audioItem.audioLink)
                this._currentAudio = CurrentAudio(
                    item = audioItem,
                    state = CurrentAudio.State.LOADING
                )
            }
            currentAudio.item == audioItem -> { // Updating current audio
                // Since we have a current audio, it's either loading or playing.
                // So the update here is to always stop the current audio.
                audioPlayer.stop()
                this._currentAudio = null
            }
            else -> { // Switching to another audio
                audioPlayer.stop()
                audioPlayer.play(audioItem.audioLink)
                this._currentAudio = CurrentAudio(
                    item = audioItem,
                    state = CurrentAudio.State.LOADING
                )
            }
        }
    }

    override fun stop() {
        audioPlayer.stop()
        _currentAudio = null
    }

    override fun preload(audioItem: AudioItem) {
        audioPlayer.preload(audioItem.audioLink)
    }

    override fun release() {
        audioPlayer.release()
        _currentAudio = null
    }

    private fun buildAudioListener(): IAudioPlayer.Listener {
        return IAudioPlayer.Listener(onStateChange = ::onAudioStateChange)
    }

    private fun onAudioStateChange(audioState: AudioState) {
        val currentAudio = _currentAudio ?: return

        val newCurrentAudio: CurrentAudio? = when (audioState) {
            AudioState.LOADING -> currentAudio.copy(state = CurrentAudio.State.LOADING)
            AudioState.PLAYING -> currentAudio.copy(state = CurrentAudio.State.PLAYING)
            AudioState.PAUSED -> null
            AudioState.STOPPED -> null
        }
        this._currentAudio = newCurrentAudio
    }
}
