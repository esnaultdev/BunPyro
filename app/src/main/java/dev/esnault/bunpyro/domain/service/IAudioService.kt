package dev.esnault.bunpyro.domain.service

import dev.esnault.bunpyro.domain.entities.media.AudioItem
import dev.esnault.bunpyro.domain.entities.media.CurrentAudio
import kotlinx.coroutines.flow.Flow


interface IAudioService {

    val currentAudio: Flow<CurrentAudio?>

    fun playOrStop(audioItem: AudioItem)

    fun stop()

    fun release()
}
