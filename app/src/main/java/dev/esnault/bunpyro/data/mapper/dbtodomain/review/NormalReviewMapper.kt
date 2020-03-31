package dev.esnault.bunpyro.data.mapper.dbtodomain.review

import dev.esnault.bunpyro.data.db.review.ReviewDb
import dev.esnault.bunpyro.data.db.review.ReviewType
import dev.esnault.bunpyro.data.mapper.IMapper
import dev.esnault.bunpyro.data.network.entities.NormalReview


class NormalReviewMapper : IMapper<NormalReview, ReviewDb> {

    override fun map(o: NormalReview): ReviewDb {
        return ReviewDb(
            id = ReviewDb.Id(o.id, ReviewType.NORMAL),
            grammarId = o.grammarId,
            createdAt = o.createdAt,
            updatedAt = o.updatedAt,
            nextReview = o.nextReview,
            lastStudiedAt = o.lastStudiedAt
        )
    }
}
