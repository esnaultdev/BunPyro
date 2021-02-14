package dev.esnault.bunpyro.domain.entities.review

import dev.esnault.bunpyro.data.db.review.ReviewType
import org.junit.Test
import java.util.*
import kotlin.test.assertEquals


class ReviewTest {

    @Test
    fun `srs level should be null if no review exists`() {
        // Given
        val review: Review? = null
        // When
        val srsLevel = review.srsLevel
        // Then
        assertEquals(expected = null, actual = srsLevel)
    }

    @Test
    fun `srs level should be null if the review is hidden`() {
        // Given
        val review = Review(
            id = 1L,
            type = ReviewType.NORMAL,
            grammarId = 1L,
            hidden = true,
            history = listOf(
                ReviewHistory(
                    questionId = 1L,
                    time = Date(1L),
                    status = true,
                    attempts = 1,
                    streak = 1
                ),
                ReviewHistory(
                    questionId = 2L,
                    time = Date(2L),
                    status = true,
                    attempts = 1,
                    streak = 2
                )
            )
        )
        // When
        val srsLevel = review.srsLevel
        // Then
        assertEquals(expected = null, actual = srsLevel)
    }

    @Test
    fun `srs level should be 0 if there is no history`() {
        // Given
        val review = Review(
            id = 1L,
            type = ReviewType.NORMAL,
            grammarId = 1L,
            hidden = false,
            history = emptyList()
        )
        // When
        val srsLevel = review.srsLevel
        // Then
        assertEquals(expected = 0, actual = srsLevel)
    }

    @Test
    fun `srs level should be the last streak`() {
        // Given
        // Given
        val review = Review(
            id = 1L,
            type = ReviewType.NORMAL,
            grammarId = 1L,
            hidden = false,
            history = listOf(
                ReviewHistory(
                    questionId = 1L,
                    time = Date(1L),
                    status = true,
                    attempts = 1,
                    streak = 1
                ),
                ReviewHistory(
                    questionId = 2L,
                    time = Date(2L),
                    status = true,
                    attempts = 1,
                    streak = 2
                ),
                ReviewHistory(
                    questionId = 3L,
                    time = Date(3L),
                    status = true,
                    attempts = 1,
                    streak = 3
                ),
                ReviewHistory(
                    questionId = 4L,
                    time = Date(4L),
                    status = false,
                    attempts = 1,
                    streak = 2
                )
            )
        )
        // When
        val srsLevel = review.srsLevel
        // Then
        assertEquals(expected = 2, actual = srsLevel)
    }
}
