package dev.esnault.bunpyro.android.utils

import org.junit.Assert.assertEquals
import org.junit.Test


class BunProTextTest {

    @Test
    fun processBunProFurigana_noFurigana() {
        // Given
        val input = "私はトムです。"

        // When
        val output = preProcessBunproFurigana(input)

        // Then
        val expected = "私はトムです。"
        assertEquals(expected, output)
    }

    @Test
    fun processBunProFurigana_notFuriganaParenthesis() {
        // Given
        val input = "私はトム（とむ）です。"

        // When
        val output = preProcessBunproFurigana(input)

        // Then
        val expected = "私はトム（とむ）です。"
        assertEquals(expected, output)
    }

    @Test
    fun processBunProFurigana_firstKanji() {
        // Given
        val input = "私（わたし）はトムです。"

        // When
        val output = preProcessBunproFurigana(input)

        // Then
        val expected = "<ruby>私<rt>わたし</rt></ruby>はトムです。"
        assertEquals(expected, output)
    }

    @Test
    fun processBunProFurigana_middleKanji() {
        // Given
        val input = "これは寿司（すし）です。"

        // When
        val output = preProcessBunproFurigana(input)

        // Then
        val expected = "これは<ruby>寿司<rt>すし</rt></ruby>です。"
        assertEquals(expected, output)
    }
}
