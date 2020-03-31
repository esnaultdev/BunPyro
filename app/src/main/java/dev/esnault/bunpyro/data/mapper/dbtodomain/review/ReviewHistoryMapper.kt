package dev.esnault.bunpyro.data.mapper.dbtodomain.review

import dev.esnault.bunpyro.data.db.review.ReviewType
import dev.esnault.bunpyro.data.db.reviewhistory.ReviewHistoryDb
import dev.esnault.bunpyro.data.network.entities.GhostReview
import dev.esnault.bunpyro.data.network.entities.NormalReview
import dev.esnault.bunpyro.data.network.entities.ReviewHistory


class ReviewHistoryMapper {

    fun mapFromGhostReviews(o: List<GhostReview>): List<ReviewHistoryDb> {
        return o.flatMap { ghostReview ->
            map(ghostReview.id, ReviewType.GHOST, ghostReview.history)
        }
    }

    fun mapFromNormalReviews(o: List<NormalReview>): List<ReviewHistoryDb> {
        return o.flatMap { normalReview ->
            map(normalReview.id, ReviewType.NORMAL, normalReview.history)
        }
    }

    fun map(reviewId: Long, reviewType: ReviewType, o: List<ReviewHistory>): List<ReviewHistoryDb> {
        val isSorted = o.asSequence()
            .zipWithNext()
            .all { (current, next) -> current.time.date.time <= next.time.date.time }

        val sorted = if (!isSorted) {
            o.sortedBy { it.time.date }
        } else {
            o
        }

        return sorted.mapIndexed { index, reviewHistory ->
            map(reviewId, reviewType, index, reviewHistory)
        }
    }

    private fun map(
        reviewId: Long,
        reviewType: ReviewType,
        index: Int,
        o: ReviewHistory
    ): ReviewHistoryDb {
        val id = ReviewHistoryDb.ItemId(
            index = index,
            reviewId = reviewId,
            reviewType = reviewType
        )

        return ReviewHistoryDb(
            id = id,
            questionId = o.questionId,
            time = o.time.date,
            status = o.status,
            attempts = o.attempts,
            streak = o.streak
        )
    }
}
