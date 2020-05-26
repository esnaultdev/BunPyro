package dev.esnault.bunpyro.android.media


enum class AudioState {
    LOADING,
    PLAYING,
    PAUSED,
    STOPPED;

    fun toSimpleState(): SimpleAudioState {
        return when (this) {
            LOADING -> SimpleAudioState.LOADING
            PLAYING -> SimpleAudioState.PLAYING
            PAUSED,
            STOPPED -> SimpleAudioState.STOPPED
        }
    }

    val playWhenReady: Boolean
        get() = this == LOADING || this == PLAYING
}

enum class SimpleAudioState {
    LOADING,
    PLAYING,
    STOPPED;

    val playWhenReady: Boolean
        get() = this == LOADING || this == PLAYING
}
