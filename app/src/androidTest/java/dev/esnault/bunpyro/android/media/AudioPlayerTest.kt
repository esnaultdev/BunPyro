package dev.esnault.bunpyro.android.media

import android.net.Uri
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class AudioUriTest {

    @Test
    fun getAudioUri_oldAudio() {
        // Given
        val url = "私だ。.mp3"

        // When
        val uri = getAudioUri(url)

        // Then
        val expected = Uri.parse("https://bunpro.jp/audio/私だ。.mp3")
        assertEquals(expected, uri)
    }

    @Test
    fun getAudioUri_newAudio() {
        // Given
        val url = "https://dk3kgylsgq3k1.cloudfront.net/audio/N3/Lesson 1/のに3118/Asher Kidd - 宿題をするのに、3時間 がかかる。.mp3"

        // When
        val uri = getAudioUri(url)

        // Then
        val expected = Uri.parse("https://dk3kgylsgq3k1.cloudfront.net/audio/N3/Lesson 1/のに3118/Asher Kidd - 宿題をするのに、3時間 がかかる。.mp3")
        assertEquals(expected, uri)
    }
}
