package dev.esnault.bunpyro.domain.entities

import dev.esnault.bunpyro.domain.utils.lazyNone


data class JlptLesson(
    val level: JLPT,
    /** List of the lessons, always of size 10 */
    val lessons: List<Lesson>
) {

    val studied: Int by lazyNone {
        lessons.sumBy { it.studied }
    }

    val size: Int by lazyNone {
        lessons.sumBy { it.size }
    }

    val completed: Boolean by lazyNone {
        size != 0 && studied == size
    }
}
