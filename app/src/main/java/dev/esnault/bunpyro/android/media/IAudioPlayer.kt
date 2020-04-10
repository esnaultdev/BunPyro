package dev.esnault.bunpyro.android.media


interface IAudioPlayer {

    class Listener(
        val onStateChange: (state: State) -> Unit
    )

    var listener: Listener?

    fun play(url: String?)
    fun stop()
    fun release()

    enum class State {
        LOADING,
        PLAYING,
        PAUSED,
        STOPPED
    }
}
