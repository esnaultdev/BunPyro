package dev.esnault.bunpyro.data.db.grammarpoint

import androidx.room.Entity
import androidx.room.Fts4


@Fts4(contentEntity = GrammarPointDb::class)
@Entity(tableName = "grammar_point_fts")
class GrammarPointFtsDb(
    val title: String,
    val yomikata: String,
    val meaning: String
)
