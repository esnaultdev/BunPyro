package dev.esnault.bunpyro.data.mapper.apitodb.review

import dev.esnault.bunpyro.data.db.examplesentence.ExampleSentenceDb
import dev.esnault.bunpyro.data.db.grammarpoint.GrammarPointDb
import dev.esnault.bunpyro.data.db.review.ReviewDb
import dev.esnault.bunpyro.data.db.review.ReviewType
import dev.esnault.bunpyro.data.db.reviewhistory.ReviewHistoryDb
import dev.esnault.bunpyro.data.db.supplementallink.SupplementalLinkDb
import dev.esnault.bunpyro.data.mapper.IMapper
import dev.esnault.bunpyro.data.network.entities.review.CurrentReview
import dev.esnault.bunpyro.data.network.entities.review.Study


object CurrentReviewDbMapper {

    class OfGrammarPoint : IMapper<CurrentReview, GrammarPointDb> {
        override fun map(o: CurrentReview): GrammarPointDb {
            val g = o.grammarPoint
            return GrammarPointDb(
                id = g.id,
                title = g.title,
                yomikata = g.yomikata,
                meaning = g.meaning,
                caution = g.caution,
                structure = g.structure,
                level = g.level,
                lesson = g.lesson,
                nuance = g.nuance,
                incomplete = g.incomplete,
                order = g.order
            )
        }
    }

    class OfExampleSentence {
        fun map(o: List<CurrentReview>): List<ExampleSentenceDb> {
            return o.flatMap(::map)
        }

        fun map(o: CurrentReview): List<ExampleSentenceDb> {
            return o.grammarPoint.sentences.map(::map)
        }

        fun map(o: Study.ExampleSentence): ExampleSentenceDb {
            return ExampleSentenceDb(
                id = o.id,
                grammarId = o.grammarId,
                japanese = o.japanese,
                english = o.english,
                nuance = o.nuance,
                audioLink = o.audioLink,
                order = o.order
            )
        }
    }

    class OfSupplementalLink {
        fun map(o: List<CurrentReview>): List<SupplementalLinkDb> {
            return o.flatMap(::map)
        }

        fun map(o: CurrentReview): List<SupplementalLinkDb> {
            return o.grammarPoint.links.map(::map)
        }

        fun map(o: Study.SupplementalLink): SupplementalLinkDb {
            return SupplementalLinkDb(
                id = o.id,
                grammarId = o.grammarId,
                site = o.site,
                link = o.link,
                description = o.description
            )
        }
    }

    class OfReview {
        fun map(o: List<CurrentReview>): List<ReviewDb> {
            return o.map(::map)
        }

        fun map(o: CurrentReview): ReviewDb {
            val id = ReviewDb.Id(
                id = o.id,
                type = ReviewType.NORMAL // Only normal reviews are provided by the API
            )
            return ReviewDb(
                id = id,
                grammarId = o.grammarPoint.id,
                createdAt = o.createdAt,
                updatedAt = o.updatedAt,
                nextReview = o.nextReview,
                lastStudiedAt = o.lastStudiedAt,
                hidden = !o.complete
            )
        }
    }

    class OfReviewHistory {
        private val historyMapper = ReviewHistoryMapper()

        fun map(o: List<CurrentReview>): List<ReviewHistoryDb> {
            return o.flatMap(::map)
        }

        fun map(o: CurrentReview): List<ReviewHistoryDb> {
            // Only normal reviews are provided by the API
            return historyMapper.map(o.id, ReviewType.NORMAL, o.history)
        }
    }
}
