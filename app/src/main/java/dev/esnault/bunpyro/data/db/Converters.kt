package dev.esnault.bunpyro.data.db

import androidx.room.TypeConverter
import dev.esnault.bunpyro.data.db.review.ReviewType
import java.util.*


class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }

    @TypeConverter
    fun fromReviewType(type: ReviewType): Int {
        return type.value
    }

    @TypeConverter
    fun toReviewType(value: Int): ReviewType {
        return ReviewType.fromValue(value)
    }
}
