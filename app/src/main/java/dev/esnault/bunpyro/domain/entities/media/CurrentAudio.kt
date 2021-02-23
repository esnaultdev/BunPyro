package dev.esnault.bunpyro.domain.entities.media

import dev.esnault.bunpyro.android.media.SimpleAudioState


data class CurrentAudio(
    val item: AudioItem,
    val state: State
) {

    /**
     * The state of the current audio.
     * Not using AudioState or SimpleAudioState since the STOPPED state is represented by the
     * lack of [CurrentAudio].
     */
    enum class State {
        LOADING, PLAYING;

        fun toSimpleState(): SimpleAudioState = when (this) {
            LOADING -> SimpleAudioState.LOADING
            PLAYING -> SimpleAudioState.PLAYING
        }
    }
}
