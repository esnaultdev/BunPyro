package dev.esnault.bunpyro.data.mapper.apitodb.review

import dev.esnault.bunpyro.common.stdlib.takeIfAllNonNull
import dev.esnault.bunpyro.data.db.review.ReviewType
import dev.esnault.bunpyro.data.db.reviewhistory.ReviewHistoryDb
import dev.esnault.bunpyro.data.network.entities.review.GhostReview
import dev.esnault.bunpyro.data.network.entities.review.NormalReview
import dev.esnault.bunpyro.data.network.entities.review.ReviewHistory


class ReviewHistoryMapper {

    fun mapFromGhostReviews(o: List<GhostReview>): List<ReviewHistoryDb> {
        return o.flatMap { ghostReview ->
            if (ghostReview.id != null && ghostReview.history != null) {
                map(ghostReview.id, ReviewType.GHOST, ghostReview.history).orEmpty()
            } else {
                emptyList()
            }
        }
    }

    fun mapFromNormalReviews(o: List<NormalReview>): List<ReviewHistoryDb> {
        return o.flatMap { normalReview ->
            if (normalReview.id != null && normalReview.history != null) {
                map(normalReview.id, ReviewType.NORMAL, normalReview.history).orEmpty()
            } else {
                emptyList()
            }
        }
    }

    fun map(
        reviewId: Long,
        reviewType: ReviewType,
        o: List<ReviewHistory>
    ): List<ReviewHistoryDb>? {
        if (o.any { it.time == null }) return null

        val isSorted = o.asSequence()
            .zipWithNext()
            .all { (current, next) -> current.time!!.date.time <= next.time!!.date.time }

        val sorted = if (!isSorted) {
            o.sortedBy { it.time!!.date }
        } else {
            o
        }

        return sorted.mapIndexed { index, reviewHistory ->
            map(reviewId, reviewType, index, reviewHistory)
        }.takeIfAllNonNull()
    }

    private fun map(
        reviewId: Long,
        reviewType: ReviewType,
        index: Int,
        o: ReviewHistory
    ): ReviewHistoryDb? {
        if (o.questionId == null ||
            o.time == null ||
            o.status == null ||
            o.attempts == null ||
            o.streak == null
        ) return null

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
