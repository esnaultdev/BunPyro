package dev.esnault.bunpyro.data.network.entities.review

import com.squareup.moshi.Json
import java.util.*


/**
 * Parsing of a current review of the Bunpro API.
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
 * These fields are ignored since we can get them from sub objects:
 * - grammar_point_id
 * - study_question_id
 *
 * These fields are ignored because I have no idea what they do:
 * - review_misses
 */
data class CurrentReview(
    val id: Long,
    @Json(name = "next_review") val nextReview: Date,
    @Json(name = "created_at") val createdAt: Date,
    @Json(name = "updated_at") val updatedAt: Date,
    @Json(name = "last_studied_at") val lastStudiedAt: Date?,
    val readings: List<Long>,
    val history: List<ReviewHistory>,
    @Json(name = "missed_question_ids") val missedQuestionIds: List<Long>,
    @Json(name = "studied_question_ids") val studiedQuestionIds: List<Long>,
    val complete: Boolean,
    @Json(name = "study_question") val studyQuestion: Study.Question,
    @Json(name = "grammar_point") val grammarPoint: Study.GrammarPoint,
    @Json(name = "review_type") val reviewType: ReviewType,
    @Json(name = "self_study") val selfStudy: Boolean
)

/**
 * Data referenced by a current review.
 * These are similar to their normal API version, but different enough that we can't reuse them.
 */
object Study {

    /**
     * Parsing of a study question of the Bunpro API.
     *
     * These fields are ignored since they seem redundant
     * - kanji_answer
     * - kanji_alt_grammar
     * - kanji_alt_answers
     * - kanji_wrong_answers
     * - alternate_japanese
     * - alternate_english
     *
     * These fields are ignored since we don't use them:
     * - last_updated_by
     */
    data class Question(
        val id: Long,
        val japanese: String,
        val english: String,
        val answer: String,
        @Json(name = "grammar_point_id") val grammarId: Long,
        @Json(name = "created_at") val createdAt: Date,
        @Json(name = "updated_at") val updatedAt: Date,
        @Json(name = "alternate_answers") val alternateAnswers: Map<String, String>,
        @Json(name = "alternate_grammar") val alternateGrammar: List<String>,
        @Json(name = "wrong_answers") val wrongAnswers: Map<String, String>,
        @Json(name = "audio") val audioLink: String?,
        val nuance: String?,
        val tense: String?,
        @Json(name = "sentence_order") val sentenceOrder: Int?
    )

    /**
     * Parsing of a study grammar point of the Bunpro API.
     *
     * These fields are ignored since they're not provided by the default grammar point API:
     * - created_at
     * - updated_at
     * - alternate
     * - formal
     * - new_grammar
     * - discourse_link
     * - last_updated_by
     */
    data class GrammarPoint(
        val id: Long,
        val title: String,
        val yomikata: String,
        val meaning: String,
        val caution: String?,
        val structure: String?,
        val level: String, // JLPT level
        @Json(name = "lesson_id") val lesson: Int,
        val nuance: String?,
        val incomplete: Boolean,
        @Json(name = "grammar_order") val order: Int?,
        @Json(name = "example_sentences") val sentences: List<ExampleSentence>,
        @Json(name = "supplemental_links") val links: List<SupplementalLink>
    )

    /**
     * Parsing of a study example sentence of the Bunpro API.
     *
     * These fields are ignored since they're not provided by the default example sentence API:
     * - structure
     * - alternate_japanese
     * - sentence_audio_id
     * - created_at
     * - updated_at
     * - last_updated_by
     */
    data class ExampleSentence(
        val id: Long,
        @Json(name = "grammar_point_id") val grammarId: Long,
        val japanese: String,
        val english: String,
        val nuance: String?,
        @Json(name = "sentence_order") val order: Int?,
        @Json(name = "audio_link") val audioLink: String?
    )

    /**
     * Parsing of a study supplemental link of the Bunpro API.
     *
     * These fields are ignored since they're not provided by the default supplemental link API:
     * - created_at
     * - updated_at
     */
    data class SupplementalLink(
        val id: Long,
        @Json(name = "grammar_point_id") val grammarId: Long,
        val site: String,
        val link: String,
        val description: String
    )
}
