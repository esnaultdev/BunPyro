package dev.esnault.bunpyro.data.db.grammarpoint

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "grammar_point")
data class GrammarPointDb(
    @PrimaryKey val id: Long,
    val title: String,
    val yomikata: String,
    val meaning: String,
    val caution: String?,
    val structure: String?,
    val level: String?,
    val lesson: Int,
    val nuance: String?,
    val incomplete: Boolean,
    @ColumnInfo(name = "grammar_order") val order: Int
)
