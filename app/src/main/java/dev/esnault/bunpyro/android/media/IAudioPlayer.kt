package dev.esnault.bunpyro.android.media


interface IAudioPlayer {

    class Listener(
        val onStateChange: (state: AudioState) -> Unit
    )

    var listener: Listener?

    fun play(url: String?)
    fun stop()
    fun release()
}
