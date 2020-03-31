package dev.esnault.bunpyro.data.db.reviewhistory

import androidx.room.*
import dev.esnault.bunpyro.data.db.review.ReviewDb
import dev.esnault.bunpyro.data.db.review.ReviewType
import java.util.*


@Entity(
    tableName = "review_history",
    foreignKeys = [
        ForeignKey(
            entity = ReviewDb::class,
            parentColumns = ["id", "type"],
            childColumns = ["review_id", "review_type"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["review_id", "review_type"])]
)
data class ReviewHistoryDb(
    @PrimaryKey
    @Embedded val id: ItemId,
    @ColumnInfo(name = "question_id") val questionId: Long,
    val time: Date,
    val status: Boolean,
    val attempts: Int,
    val streak: Int
) {

    data class ItemId(
        @ColumnInfo(name = "history_index") val index: Int,
        @ColumnInfo(name = "review_id") val reviewId: Long,
        @ColumnInfo(name = "review_type") val reviewType: ReviewType
    )
}
