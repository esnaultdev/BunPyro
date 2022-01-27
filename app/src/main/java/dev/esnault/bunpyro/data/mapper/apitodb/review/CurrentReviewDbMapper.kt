package dev.esnault.bunpyro.data.mapper.apitodb.review

import dev.esnault.bunpyro.data.db.examplesentence.ExampleSentenceDb
import dev.esnault.bunpyro.data.db.grammarpoint.GrammarPointDb
import dev.esnault.bunpyro.data.db.review.ReviewDb
import dev.esnault.bunpyro.data.db.review.ReviewType as ReviewTypeDb
import dev.esnault.bunpyro.data.db.reviewhistory.ReviewHistoryDb
import dev.esnault.bunpyro.data.db.supplementallink.SupplementalLinkDb
import dev.esnault.bunpyro.data.mapper.INullableMapper
import dev.esnault.bunpyro.data.network.entities.review.CurrentReview
import dev.esnault.bunpyro.data.network.entities.review.ReviewType
import dev.esnault.bunpyro.data.network.entities.review.Study


object CurrentReviewDbMapper {

    class OfGrammarPoint : INullableMapper<CurrentReview, GrammarPointDb> {
        override fun map(o: CurrentReview): GrammarPointDb? {
            val g = o.grammarPoint

            @Suppress("NullChecksToSafeCall")
            if (g == null ||
                g.id == null ||
                g.title == null ||
                g.yomikata == null ||
                g.meaning == null ||
                g.lesson == null
            ) return null

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
                order = g.order ?: 0
            )
        }
    }

    class OfExampleSentence {
        fun map(o: List<CurrentReview>): List<ExampleSentenceDb> {
            return o.flatMap { map(it).orEmpty() }
        }

        fun map(o: CurrentReview): List<ExampleSentenceDb>? {
            return o.grammarPoint?.sentences?.mapNotNull(::map)
        }

        fun map(o: Study.ExampleSentence): ExampleSentenceDb? {
            if (o.id == null ||
                o.grammarId == null ||
                o.japanese == null ||
                o.english == null
            ) return null

            return ExampleSentenceDb(
                id = o.id,
                grammarId = o.grammarId,
                japanese = o.japanese,
                english = o.english,
                nuance = o.nuance,
                audioLink = o.audioLink,
                order = o.order ?: 0
            )
        }
    }

    class OfSupplementalLink {
        fun map(o: List<CurrentReview>): List<SupplementalLinkDb> {
            return o.flatMap { map(it).orEmpty() }
        }

        fun map(o: CurrentReview): List<SupplementalLinkDb>? {
            return o.grammarPoint?.links?.mapNotNull(::map)
        }

        fun map(o: Study.SupplementalLink): SupplementalLinkDb? {
            if (o.id == null ||
                o.grammarId == null ||
                o.site == null ||
                o.link == null ||
                o.description == null
            ) return null

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
            return o.mapNotNull(::map)
        }

        fun map(o: CurrentReview): ReviewDb? {
            val type = o.reviewType?.let(OfReviewType::map)
            if (o.id == null ||
                type == null ||
                o.grammarPoint == null ||
                o.grammarPoint.id == null ||
                o.createdAt == null ||
                o.updatedAt == null ||
                o.nextReview == null
            ) return null

            val id = ReviewDb.Id(
                id = o.id,
                type = type,
            )
            return ReviewDb(
                id = id,
                grammarId = o.grammarPoint.id,
                createdAt = o.createdAt,
                updatedAt = o.updatedAt,
                nextReview = o.nextReview,
                lastStudiedAt = o.lastStudiedAt,
                hidden = !o.complete,
            )
        }
    }

    class OfReviewHistory {
        private val historyMapper = ReviewHistoryMapper()

        fun map(o: List<CurrentReview>): List<ReviewHistoryDb> {
            return o.flatMap { map(it).orEmpty() }
        }

        fun map(o: CurrentReview): List<ReviewHistoryDb>? {
            if (o.id == null
                || o.reviewType == null
                || o.history == null
            ) return null
            return historyMapper.map(o.id, OfReviewType.map(o.reviewType), o.history)
        }
    }

    object OfReviewType {
        fun map(o: ReviewType): ReviewTypeDb {
            return when (o) {
                ReviewType.NORMAL -> ReviewTypeDb.NORMAL
                ReviewType.GHOST -> ReviewTypeDb.GHOST
            }
        }
    }
}
