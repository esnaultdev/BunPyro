package dev.esnault.bunpyro.data.network.entities

import com.squareup.moshi.Json
import java.util.*


/**
 * Parsing of a normal review of the Bunpro API.
 *
 * user_id is ignored since we're only having one user in the app
 *
 * These fields are ignored since we can compute them from the history
 * - times_correct
 * - times_incorrect
 * - streak
 * - max_streak
 * - was_correct
 *
 * These fields are ignored since the review type is already known by the review array:
 * - review_type
 * - self_study
 *
 * These fields are ignored because I have no idea what they do:
 * - complete
 * - review_misses
 */
data class NormalReview(
    val id: Int,
    @Json(name = "study_question_id") val questionId: Int,
    @Json(name = "grammar_point_id") val grammarId: Int,
    @Json(name = "next_review") val nextReview: Date,
    @Json(name = "created_at") val createdAt: Date,
    @Json(name = "updated_at") val updatedAt: Date,
    @Json(name = "last_studied_at") val lastStudiedAt: Date?,
    val readings: List<Int>,
    val history: List<History>,
    @Json(name = "missed_question_ids") val missedQuestionIds: List<Int>,
    @Json(name = "studied_question_ids") val studiedQuestionIds: List<Int>
) {

    data class History(
        @Json(name = "id") val questionId: Int,
        val time: BunProDate,
        val status: Boolean,
        val attempts: Int,
        val streak: Int
    )
}
