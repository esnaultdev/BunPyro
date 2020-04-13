package dev.esnault.bunpyro.data.db.grammarpoint

import androidx.room.Embedded
import androidx.room.Relation
import dev.esnault.bunpyro.data.db.examplesentence.ExampleSentenceDb
import dev.esnault.bunpyro.data.db.review.ReviewDb
import dev.esnault.bunpyro.data.db.reviewhistory.ReviewHistoryDb
import dev.esnault.bunpyro.data.db.supplementallink.SupplementalLinkDb


data class FullGrammarPointDb(
    @Embedded val point: GrammarPointDb,
    @Relation(
        parentColumn = "id",
        entityColumn = "grammar_id"
    )
    val sentences: List<ExampleSentenceDb>,
    @Relation(
        parentColumn = "id",
        entityColumn = "grammar_id"
    )
    val links: List<SupplementalLinkDb>,
    @Relation(
        parentColumn = "id",
        entityColumn = "grammar_id",
        entity = ReviewDb::class
    )
    val reviews: List<FullReviewDb>
)

data class FullReviewDb(
    @Embedded val review: ReviewDb,
    @Relation(
        parentColumn = "id_type",
        entityColumn = "review_id_type"
    )
    val history: List<ReviewHistoryDb>
)

