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
    indices = [
        Index(value = ["review_id", "review_type"]),
        Index(value = ["review_id_type"])
    ]
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

    /**
     * Dummy column used to have a @Relation with a composite key.
     * Room needs a backing field with both getters and setters, but let's
     * prevent setting this field since it could be a source of bugs.
     */
    @ColumnInfo(name = "review_id_type")
    @Suppress("SetterBackingFieldAssignment")
    var reviewIdType: String = "${id.reviewId}_${id.reviewType.value}"
        set(value) {}

    data class ItemId(
        @ColumnInfo(name = "history_index") val index: Int,
        @ColumnInfo(name = "review_id") val reviewId: Long,
        @ColumnInfo(name = "review_type") val reviewType: ReviewType
    )
}
