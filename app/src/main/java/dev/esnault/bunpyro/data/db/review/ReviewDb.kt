package dev.esnault.bunpyro.data.db.review

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*


@Entity(tableName = "review")
data class ReviewDb(
    @PrimaryKey val id: Long,
    val type: ReviewType,
    @ColumnInfo(name = "grammar_id") val grammarId: Long,
    @ColumnInfo(name = "created_at") val createdAt: Date,
    @ColumnInfo(name = "updated_at") val updatedAt: Date,
    @ColumnInfo(name = "next_review") val nextReview: Date,
    @ColumnInfo(name = "last_studied_at") val lastStudiedAt: Date?
)
