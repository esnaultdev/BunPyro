package dev.esnault.bunpyro.data.db.supplementallink

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import dev.esnault.bunpyro.data.db.grammarpoint.GrammarPointDb


@Entity(
    tableName = "supplemental_link",
    foreignKeys = [
        ForeignKey(
            entity = GrammarPointDb::class,
            parentColumns = ["id"],
            childColumns = ["grammar_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class SupplementalLinkDb(
    @PrimaryKey val id: Int,
    @ColumnInfo(name = "grammar_id", index = true) val grammarId: Int,
    val site: String,
    val link: String,
    val description: String
)
