package dev.esnault.bunpyro.domain.entities.review

import org.junit.Test
import kotlin.test.assertEquals


class ReviewSessionTest {

    // region Progress

    @Test
    fun `precision should be 1 at the start of a session`() {
        // Given
        val progress = ReviewSession.Progress(4, 0, 0)
        // When
        val precision = progress.precision
        // Then
        assertEquals(expected = 1f, actual = precision)
    }

    @Test
    fun `precision should be 1 if there are only good answers`() {
        // Given
        val progress = ReviewSession.Progress(4, 1, 0)
        // When
        val precision = progress.precision
        // Then
        assertEquals(expected = 1f, actual = precision)
    }

    @Test
    fun `precision should be 0_5f if there are as many good answers as bad answers`() {
        // Given
        val progress = ReviewSession.Progress(4, 2, 2)
        // When
        val precision = progress.precision
        // Then
        assertEquals(expected = 0.5f, actual = precision)
    }

    @Test
    fun `precision should be the same during ask agains`() {
        // Given
        val progress = ReviewSession.Progress(10, 8, 5)
        // When
        val precision = progress.precision
        // Then
        assertEquals(expected = 0.5f, actual = precision)
    }

    @Test
    fun `precision should be the same after ask agains`() {
        // Given
        val progress = ReviewSession.Progress(10, 10, 5)
        // When
        val precision = progress.precision
        // Then
        assertEquals(expected = 0.5f, actual = precision)
    }

    // endregion
}
