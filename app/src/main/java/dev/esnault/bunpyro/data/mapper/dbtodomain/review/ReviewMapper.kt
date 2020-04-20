package dev.esnault.bunpyro.data.mapper.dbtodomain.review

import dev.esnault.bunpyro.data.db.grammarpoint.FullReviewDb
import dev.esnault.bunpyro.data.mapper.IMapper
import dev.esnault.bunpyro.domain.entities.review.Review


class ReviewMapper : IMapper<FullReviewDb, Review> {

    private val historyMapper = ReviewHistoryMapper()

    override fun map(o: FullReviewDb): Review {
        return Review(
            id = o.review.id.id,
            type = o.review.id.type,
            grammarId = o.review.grammarId,
            hidden = o.review.hidden,
            history = historyMapper.map(o.history)
        )
    }
}
