package dev.esnault.bunpyro.data.db.examplesentence

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.PrimaryKey
import dev.esnault.bunpyro.data.db.grammarpoint.GrammarPointDb


@Entity(
    tableName = "example_sentence",
    foreignKeys = [
        ForeignKey(
            entity = GrammarPointDb::class,
            parentColumns = ["id"],
            childColumns = ["grammar_id"],
            onDelete = CASCADE
        )
    ]
)
data class ExampleSentenceDb(
    @PrimaryKey val id: Int,
    @ColumnInfo(name = "grammar_id") val grammarId: Int,
    val japanese: String,
    val english: String,
    val nuance: String?,
    val audioLink: String?,
    @ColumnInfo(name = "sentence_order") val order: Int
)
