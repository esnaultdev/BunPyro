package dev.esnault.bunpyro.data.mapper.apitodb.review

import dev.esnault.bunpyro.data.db.review.ReviewDb
import dev.esnault.bunpyro.data.db.review.ReviewType
import dev.esnault.bunpyro.data.mapper.INullableMapper
import dev.esnault.bunpyro.data.network.entities.review.GhostReview


class GhostReviewMapper : INullableMapper<GhostReview, ReviewDb> {

    override fun map(o: GhostReview): ReviewDb? {
        if (o.id == null ||
            o.grammarId == null ||
            o.createdAt == null ||
            o.updatedAt == null ||
            o.nextReview == null
        ) return null

        return ReviewDb(
            id = ReviewDb.Id(o.id, ReviewType.GHOST),
            grammarId = o.grammarId,
            createdAt = o.createdAt,
            updatedAt = o.updatedAt,
            nextReview = o.nextReview,
            lastStudiedAt = o.lastStudiedAt,
            hidden = false
        )
    }
}
