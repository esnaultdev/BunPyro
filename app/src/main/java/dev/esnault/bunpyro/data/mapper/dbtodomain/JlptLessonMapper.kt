package dev.esnault.bunpyro.data.mapper.dbtodomain

import dev.esnault.bunpyro.data.db.grammarpoint.GrammarPointOverviewDb
import dev.esnault.bunpyro.domain.entities.JLPT
import dev.esnault.bunpyro.domain.entities.JlptLesson
import dev.esnault.bunpyro.domain.entities.Lesson


class JlptLessonMapper {

    private val grammarPointMapper = GrammarPointOverviewMapper()

    fun map(o: List<GrammarPointOverviewDb>): List<JlptLesson> {
        val pointsByLesson = o.groupBy(
            keySelector = { it.lesson },
            valueTransform = grammarPointMapper::map
        )

        // Each JLPT level has 10 lessons, with ids from 1 to 50:
        // N5: lessons 1 to 10, N4: lessons 11 to 20, etc.
        return (1..5).map { i ->
            val jlpt = JLPT[6 - i]
            val lessons = (1..10).map { j ->
                val lessonId = ((i - 1) * 10) + j
                Lesson(lessonId, pointsByLesson[lessonId] ?: emptyList())
            }
            JlptLesson(
                level = jlpt,
                lessons = lessons
            )
        }
    }
}
