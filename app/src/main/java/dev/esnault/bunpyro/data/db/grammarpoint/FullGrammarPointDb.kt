package dev.esnault.bunpyro.data.db.grammarpoint

import androidx.room.Embedded
import androidx.room.Relation
import dev.esnault.bunpyro.data.db.examplesentence.ExampleSentenceDb
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
    val links: List<SupplementalLinkDb>
)

