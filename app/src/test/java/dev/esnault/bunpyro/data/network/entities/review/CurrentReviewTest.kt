package dev.esnault.bunpyro.data.network.entities.review

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import dev.esnault.bunpyro.KoinTestRule
import dev.esnault.bunpyro.data.network.entities.BunProDate
import dev.esnault.bunpyro.di.networkModule
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.koin.test.KoinTest
import org.koin.test.inject
import java.util.*


class CurrentReviewTest : KoinTest {

    val moshi: Moshi by inject()

    @get:Rule
    val koinTestRule = KoinTestRule.create {
        modules(networkModule)
    }

    @Test
    fun parseCurrentReviews() {
        // Given
        val input = currentReviews
        val type = Types.newParameterizedType(List::class.java, CurrentReview::class.java)
        val adapter: JsonAdapter<List<CurrentReview>> = moshi.adapter(type)

        // When
        val output: List<CurrentReview>? = adapter.fromJson(input)

        // Then
        val expected = listOf(
            CurrentReview(
                id = 1631846L,
                nextReview = Date(1586277000000L),
                createdAt = Date(1577960695059L),
                updatedAt = Date(1586234866521L),
                lastStudiedAt = Date(1586234866517L),
                readings = emptyList(),
                history = listOf(
                    ReviewHistory(
                        questionId = 184,
                        time = BunProDate(Date(1577959200000L)),
                        status = false,
                        attempts = 2,
                        streak = 0
                    ),
                    ReviewHistory(
                        questionId = 185,
                        time = BunProDate(Date(1577962800000L)),
                        status = true,
                        attempts = 1,
                        streak = 1
                    )
                ),
                missedQuestionIds = listOf(184),
                studiedQuestionIds = listOf(184, 185),
                complete = true,
                studyQuestion = Study.Question(
                    id = 2667L,
                    japanese = "<span class='study-area-input'>____</span>土（つち）の上（うえ）は歩（ある）かない方（ほう）がいいよ。[崩（くず）れる]",
                    english = "You should not walk on top of ground that <strong>looks like it could crumble</strong>.",
                    answer = "くずれそうな",
                    grammarId = 118L,
                    createdAt = Date(1517981905319L),
                    updatedAt = Date(1568297515220L),
                    alternateAnswers = mapOf(
                        "くずれげな" to "This is more subjective and less certain. Looking for something more objective/certain."
                    ),
                    alternateGrammar = emptyList(),
                    wrongAnswers = mapOf(
                        "はずがありませんでした" to "You need a な between はず and a なAdj."
                    ),
                    audioLink = "崩れそうな土の上は歩かない方がいいよ。.mp3",
                    nuance = "[Prediction・conjecture/guess・based on visual cues]",
                    tense = "[objective・low confidence]",
                    sentenceOrder = 11
                ),
                grammarPoint = Study.GrammarPoint(
                    id = 118L,
                    title = "そうに/そうな ",
                    yomikata = "そうに/そうな ",
                    meaning = "seem, look like, sound",
                    caution = "Negative Verb[な<del>い</del>] + <strong>そう<span class='chui'></strong>, い-Adj [な<del>い</del>] + <span class='chui'>さ</span> + <strong>そう<span class='chui'></strong>",
                    structure = "なAdj + <strong>そう<span class='chui'>に</span></strong> + Verb, なAdj + <strong>そう<span class='chui'>な</span></strong> + Noun, いAdj[<del>い</del>] + <strong>そう<span class='chui'>に</span></strong> + Verb, いAdj[<del>い</del>] + <strong>そう<span class='chui'>な</span></strong> + Noun, Verb[<span class='chui'>stem</span>] + <strong>そう<span class='chui'>に</span></strong> + Verb,  Verb[<span class='chui'>stem</span>] + <strong>そう<span class='chui'>な</span></strong> + Noun",
                    level = "JLPT4",
                    lesson = 13,
                    nuance = "[~そう → more objective・~げ → more subjective and less certain]<br><br>[Prediction・conjecture/guess・based on visual cues・low confidence]<br>[Sometimes either な or に can be used. そうな places more emphasis on the noun ・そうに places more emphasis on the act of doing (verb)]",
                    incomplete = false,
                    order = 152,
                    sentences = listOf(
                        Study.ExampleSentence(
                            id = 2476L,
                            grammarId = 118L,
                            japanese = "優（やさ）しそうに犬（いぬ）を撫（な）でた。",
                            english = "She petted the dog in a way that <strong>seemed</strong> affectionate.",
                            nuance = "",
                            order = 4,
                            audioLink = "優しそうに犬を撫でた。.mp3"
                        )
                    ),
                    links = listOf(
                        Study.SupplementalLink(
                            id = 654L,
                            grammarId = 118L,
                            site = "Japanese Test 4 You",
                            link = "http://japanesetest4you.com/flashcard/learn-jlpt-n4-grammar-%E3%81%9D%E3%81%86%E3%81%AB-%E3%81%9D%E3%81%86%E3%81%AA-sou-ni-sou-na/",
                            description = "Request Lesson : そうに and そうな Listening Practice "
                        )
                    )
                ),
                reviewType = ReviewType.NORMAL,
                selfStudy = false
            )
        )
        assertEquals(expected, output)
    }
}

const val currentReviews = """
[
  {
    "id": 1631846,
    "user_id": 18511,
    "study_question_id": 2667,
    "grammar_point_id": 118,
    "times_correct": 1,
    "times_incorrect": 1,
    "streak": 1,
    "next_review": "2020-04-07T16:30:00.000Z",
    "created_at": "2020-01-02T10:24:55.059Z",
    "updated_at": "2020-04-07T04:47:46.521Z",
    "readings": [],
    "complete": true,
    "last_studied_at": "2020-04-07T04:47:46.517Z",
    "was_correct": false,
    "self_study": false,
    "review_misses": 0,
    "history": [
      {
        "id": 184,
        "time": "2020-01-02 10:00:00 +0000",
        "status": false,
        "attempts": 2,
        "streak": 0
      },
      {
        "id": 185,
        "time": "2020-01-02 11:00:00 +0000",
        "status": true,
        "attempts": 1,
        "streak": 1
      }
    ],
    "missed_question_ids": [
      184
    ],
    "studied_question_ids": [
      184,
      185
    ],
    "review_type": "standard",
    "max_streak": 4,
    "study_question": {
      "id": 2667,
      "japanese": "<span class='study-area-input'>____</span>土（つち）の上（うえ）は歩（ある）かない方（ほう）がいいよ。[崩（くず）れる]",
      "english": "You should not walk on top of ground that <strong>looks like it could crumble</strong>.",
      "answer": "くずれそうな",
      "grammar_point_id": 118,
      "created_at": "2018-02-07T05:38:25.319Z",
      "updated_at": "2019-09-12T14:11:55.220Z",
      "alternate_answers": {
        "くずれげな": "This is more subjective and less certain. Looking for something more objective/certain."
      },
      "alternate_grammar": [],
      "wrong_answers": {
        "はずがありませんでした": "You need a な between はず and a なAdj."
      },
      "kanji_answer": "くずれそうな",
      "kanji_alt_grammar": [],
      "kanji_alt_answers": {},
      "kanji_wrong_answers": {},
      "alternate_japanese": {},
      "alternate_english": [],
      "audio": "崩れそうな土の上は歩かない方がいいよ。.mp3",
      "nuance": "[Prediction・conjecture/guess・based on visual cues]",
      "sentence_order": 11,
      "last_updated_by": "Pushindawood",
      "tense": "[objective・low confidence]"
    },
    "grammar_point": {
      "id": 118,
      "title": "そうに/そうな ",
      "created_at": "2017-09-26T04:02:31.220Z",
      "updated_at": "2020-03-23T21:22:20.667Z",
      "alternate": "",
      "meaning": "seem, look like, sound",
      "caution": "Negative Verb[な<del>い</del>] + <strong>そう<span class='chui'></strong>, い-Adj [な<del>い</del>] + <span class='chui'>さ</span> + <strong>そう<span class='chui'></strong>",
      "structure": "なAdj + <strong>そう<span class='chui'>に</span></strong> + Verb, なAdj + <strong>そう<span class='chui'>な</span></strong> + Noun, いAdj[<del>い</del>] + <strong>そう<span class='chui'>に</span></strong> + Verb, いAdj[<del>い</del>] + <strong>そう<span class='chui'>な</span></strong> + Noun, Verb[<span class='chui'>stem</span>] + <strong>そう<span class='chui'>に</span></strong> + Verb,  Verb[<span class='chui'>stem</span>] + <strong>そう<span class='chui'>な</span></strong> + Noun",
      "formal": false,
      "level": "JLPT4",
      "lesson_id": 13,
      "new_grammar": false,
      "yomikata": "そうに/そうな ",
      "nuance": "[~そう → more objective・~げ → more subjective and less certain]<br><br>[Prediction・conjecture/guess・based on visual cues・low confidence]<br>[Sometimes either な or に can be used. そうな places more emphasis on the noun ・そうに places more emphasis on the act of doing (verb)]",
      "discourse_link": "https://community.bunpro.jp/t/grammar-discussion/3968",
      "incomplete": false,
      "grammar_order": 152,
      "last_updated_by": "Pushindawood",
      "example_sentences": [
        {
          "id": 2476,
          "grammar_point_id": 118,
          "structure": "そうに",
          "japanese": "優（やさ）しそうに犬（いぬ）を撫（な）でた。",
          "english": "She petted the dog in a way that <strong>seemed</strong> affectionate.",
          "alternate_japanese": "",
          "created_at": "2018-02-07T05:38:23.655Z",
          "updated_at": "2019-12-12T15:01:59.456Z",
          "audio_link": "優しそうに犬を撫でた。.mp3",
          "sentence_audio_id": 452,
          "nuance": "",
          "sentence_order": 4,
          "last_updated_by": "Pushindawood"
        }
      ],
      "supplemental_links": [
        {
          "id": 654,
          "grammar_point_id": 118,
          "site": "Japanese Test 4 You",
          "link": "http://japanesetest4you.com/flashcard/learn-jlpt-n4-grammar-%E3%81%9D%E3%81%86%E3%81%AB-%E3%81%9D%E3%81%86%E3%81%AA-sou-ni-sou-na/",
          "description": "Request Lesson : そうに and そうな Listening Practice ",
          "created_at": "2017-10-29T06:35:18.415Z",
          "updated_at": "2017-10-29T06:35:18.415Z"
        }
      ]
    }
  }
]
"""
