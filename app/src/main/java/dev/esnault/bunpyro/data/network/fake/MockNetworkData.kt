package dev.esnault.bunpyro.data.network.fake

import dev.esnault.bunpyro.data.network.entities.*
import java.util.*


object Mock {
    val userInfo = UserInfo(
        userName = "Fake User",
        grammarPointCount = 0,
        ghostReviewCount = 0,
        creationDate = 0L
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
                    japanese = "偽物にご注意。",
                    english = "Beware of imitations.",
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
}
