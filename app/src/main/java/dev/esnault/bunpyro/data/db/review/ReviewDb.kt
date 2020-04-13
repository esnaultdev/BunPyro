package dev.esnault.bunpyro.data.db.review

import androidx.room.*
import java.util.*


@Entity(
    tableName = "review",
    indices = [Index(value = ["id_type"])]
)
data class ReviewDb(
    @PrimaryKey @Embedded val id: Id,
    @ColumnInfo(name = "grammar_id") val grammarId: Long,
    @ColumnInfo(name = "created_at") val createdAt: Date,
    @ColumnInfo(name = "updated_at") val updatedAt: Date,
    @ColumnInfo(name = "next_review") val nextReview: Date,
    @ColumnInfo(name = "last_studied_at") val lastStudiedAt: Date?
) {

    /**
     * Dummy column used to have a @Relation with a composite key.
     * Room needs a backing field with both getters and setters, but let's
     * prevent setting this field since it could be a source of bugs.
     */
    @ColumnInfo(name = "id_type")
    @Suppress("SetterBackingFieldAssignment")
    var idType: String = "${id.id}_${id.type.value}"
        set(value) {}

    data class Id(
        val id: Long,
        val type: ReviewType
    )
}
