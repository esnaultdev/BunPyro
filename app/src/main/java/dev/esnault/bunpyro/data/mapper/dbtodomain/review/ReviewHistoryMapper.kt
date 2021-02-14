package dev.esnault.bunpyro.data.mapper.dbtodomain.review

import dev.esnault.bunpyro.data.db.reviewhistory.ReviewHistoryDb
import dev.esnault.bunpyro.domain.entities.review.ReviewHistory


class ReviewHistoryMapper {

    fun map(o: List<ReviewHistoryDb>): List<ReviewHistory> {
        // The index is used to preserve the order of the list
        return o.sortedBy { it.id.index }.map(this::map)
    }

    private fun map(o: ReviewHistoryDb): ReviewHistory {
        return ReviewHistory(
            questionId = o.questionId,
            time = o.time,
            status = o.status,
            attempts = o.attempts,
            streak = o.streak
        )
    }
}
