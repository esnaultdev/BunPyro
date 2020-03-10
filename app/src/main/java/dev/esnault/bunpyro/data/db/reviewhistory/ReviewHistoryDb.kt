package dev.esnault.bunpyro.data.db.reviewhistory

import androidx.room.*
import dev.esnault.bunpyro.data.db.review.ReviewDb
import java.util.*


@Entity(
    tableName = "review_history",
    foreignKeys = [
        ForeignKey(
            entity = ReviewDb::class,
            parentColumns = ["id"],
            childColumns = ["review_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("review_id")]
)
data class ReviewHistoryDb(
    @PrimaryKey
    @Embedded val id: ItemId,
    @ColumnInfo(name = "question_id") val questionId: Int,
    val time: Date,
    val status: Boolean,
    val attempts: Int,
    val streak: Int
) {

    data class ItemId(
        @ColumnInfo(name = "history_index") val index: Int,
        @ColumnInfo(name = "review_id") val reviewId: Int
    )
}
