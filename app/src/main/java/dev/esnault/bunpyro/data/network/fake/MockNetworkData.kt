package dev.esnault.bunpyro.data.network.fake

import dev.esnault.bunpyro.data.network.entities.*
import dev.esnault.bunpyro.data.network.entities.review.*
import dev.esnault.bunpyro.data.network.entities.user.FullUserInfo
import dev.esnault.bunpyro.data.network.entities.user.LightUserInfo
import java.util.*


object Mock {
    val lightUserInfo = LightUserInfo(
        userName = "Fake User",
        grammarPointCount = 0,
        ghostReviewCount = 0,
        creationDate = 0L
    )

    val fullUserInfo = FullUserInfo(
        id = 123L,
        userName = "Fake User",
        subscriber = true
    )

    val grammarPoints = DataRequest(
        data = listOf(
            GrammarPoint(
                1L,
                attributes = GrammarPoint.Attributes(
                    title = "偽物",
                    yomikata = "にせもの",
                    meaning = "Fake",
                    caution = null,
                    structure = null,
                    level = "N5",
                    lesson = 1,
                    nuance = null,
                    incomplete = false,
                    grammarOrder = 1
                )
            )
        )
    )

    val exampleSentences = DataRequest(
        listOf(
            ExampleSentence(
                id = 1L,
                attributes = ExampleSentence.Attributes(
                    grammarId = 1L,
                    japanese = "<strong><ruby>偽物<rt>にせもの</rt></ruby></strong>にご<ruby>注意<rt>ちゅうい</rt></ruby>。",
                    english = "Beware of <strong>imitations</strong>.",
                    nuance = null,
                    order = 1,
                    audioLink = null
                )
            )
        )
    )

    val supplementalLinks = DataRequest(
        listOf(
            SupplementalLink(
                id = 1L,
                attributes = SupplementalLink.Attributes(
                    grammarId = 1L,
                    site = "Jisho.org",
                    link = "https://jisho.org/word/%E5%81%BD%E7%89%A9",
                    description = "偽物"
                )
            )
        )
    )

    val allReviews = ReviewsData(
        reviews = listOf(
            NormalReview(
                id = 1L,
                questionId = 1L,
                grammarId = 1L,
                nextReview = Date(0L),
                createdAt = Date(0L),
                updatedAt = Date(0L),
                lastStudiedAt = Date(0L),
                readings = listOf(1L),
                history = listOf(
                    ReviewHistory(
                        1L,
                        BunProDate(Date(0L)),
                        status = true,
                        attempts = 1,
                        streak = 1
                    )
                ),
                missedQuestionIds = emptyList(),
                studiedQuestionIds = listOf(1L),
                complete = true
            )
        ),
        ghostReviews = emptyList()
    )

    // region Current review

    private fun fakeReviewGrammarPoint(id: Long) = Study.GrammarPoint(
        id = id,
        title = "偽物 #$id",
        yomikata = "にせもの",
        meaning = "Fake",
        caution = null,
        structure = null,
        level = "N5",
        lesson = 1,
        nuance = null,
        incomplete = false,
        order = 1,
        sentences = listOf(
            Study.ExampleSentence(
                id = id,
                grammarId = id,
                japanese = "偽物にご注意。 #$id",
                english = "Beware of imitations. #$id",
                nuance = null,
                order = 1,
                audioLink = "夏休みでもいいですから、します。.mp3"
            )
        ),
        links = listOf(
            Study.SupplementalLink(
                id = id,
                grammarId = id,
                site = "Jisho.org",
                link = "https://jisho.org/word/%E5%81%BD%E7%89%A9",
                description = "偽物 #$id"
            )
        )
    )

    private fun fakeReview1(id: Long) = CurrentReview(
        id = id,
        nextReview = Date(0L),
        createdAt = Date(0L),
        updatedAt = Date(0L),
        lastStudiedAt = Date(0L),
        readings = listOf(1L),
        history = listOf(
            ReviewHistory(
                questionId = id,
                time = BunProDate(Date(0L)),
                status = true,
                attempts = 1,
                streak = 1
            )
        ),
        missedQuestionIds = emptyList(),
        studiedQuestionIds = listOf(id),
        complete = true,
        studyQuestion = Study.Question(
            id = id,
            japanese = "<span class='study-area-input'>____</span>にご注意（ちゅうい）。 #$id",
            english = "Beware of <strong>imitations</strong>. #$id",
            alternateAnswers = mapOf(
                "フェイク" to "Not Gairaigo."
            ),
            answer = "にせもの",
            wrongAnswers = emptyMap(),
            alternateGrammar = listOf("にせ"),
            audioLink = "夏休みでもいいですから、します。.mp3",
            createdAt = Date(1L),
            updatedAt = Date(1L),
            grammarId = id,
            nuance = "[this is just the vocabulary word for imitations]",
            sentenceOrder = 0,
            tense = "[present]"
        ),
        grammarPoint = fakeReviewGrammarPoint(id),
        reviewType = ReviewType.NORMAL,
        selfStudy = false
    )
    private fun fakeReview2(id: Long) = CurrentReview(
        id = id,
        nextReview = Date(0L),
        createdAt = Date(0L),
        updatedAt = Date(0L),
        lastStudiedAt = Date(0L),
        readings = listOf(1L),
        history = emptyList(),
        missedQuestionIds = emptyList(),
        studiedQuestionIds = listOf(id),
        complete = true,
        studyQuestion = Study.Question(
            id = id,
            japanese = "<span class='study-area-input'>____</span>と<span class='study-area-input'>____</span>にご注意（ちゅうい）。 #$id",
            english = "Beware of <strong>imitations</strong> and <strong>imitations</strong>. #$id",
            alternateAnswers = mapOf(
                "フェイク" to "Not Gairaigo."
            ),
            answer = "にせもの",
            wrongAnswers = emptyMap(),
            alternateGrammar = listOf("にせ", "にせにせ"),
            audioLink = "夏休みでもいいですから、します。.mp3",
            createdAt = Date(1L),
            updatedAt = Date(1L),
            grammarId = id,
            nuance = "[this is just the vocabulary word for imitations]",
            sentenceOrder = 0,
            tense = "[present]"
        ),
        grammarPoint = fakeReviewGrammarPoint(id),
        reviewType = ReviewType.NORMAL,
        selfStudy = false
    )

    val currentReviews = listOf(
        fakeReview1(1L),
        fakeReview2(2L),
        fakeReview1(3L)
    )
    val reviewCount = currentReviews.size

    // endregion
}
