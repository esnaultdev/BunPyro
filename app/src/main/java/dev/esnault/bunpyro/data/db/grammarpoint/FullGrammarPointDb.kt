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
    // TODO this relation isn't right (it's a composite foreign key, but that's not supported
    // by room right now :(
    @Relation(
        parentColumn = "id",
        entityColumn = "review_id"
    )
    val history: List<ReviewHistoryDb>
)

