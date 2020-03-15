package dev.esnault.bunpyro.data.mapper.dbtodomain

import dev.esnault.bunpyro.data.db.grammarpoint.LessonProgressDb
import dev.esnault.bunpyro.domain.entities.JlptProgress


class JlptProgressMapper {

    fun map(o: List<LessonProgressDb>): JlptProgress {
        val lessonMap = o.associateBy { it.lesson }

        // Each JLPT level has 10 lessons, with ids from 1 to 50.
        // N5: lessons 1 to 10, N4: lessons 11 to 20, etc.
        fun lessonProgress(jlptLevel: Int): JlptProgress.Progress {
            val lessons = (1..10).mapNotNull { j ->
                val lessonId = ((5 - jlptLevel) * 10) + j
                lessonMap[lessonId]
            }
            return JlptProgress.Progress(
                studied = lessons.sumBy { it.studied },
                total = lessons.sumBy { it.total }
            )
        }

        return JlptProgress(
            n5 = lessonProgress(5),
            n4 = lessonProgress(4),
            n3 = lessonProgress(3),
            n2 = lessonProgress(2),
            n1 = lessonProgress(1)
        )
    }
}
