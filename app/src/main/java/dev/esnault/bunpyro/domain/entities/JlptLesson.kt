package dev.esnault.bunpyro.domain.entities


data class JlptLesson(
    val level: JLPT,
    /** List of the lessons, always of size 10 */
    val lessons: List<Lesson>
) {

    val studied: Int by lazy {
        lessons.sumBy { it.studied }
    }

    val size: Int by lazy {
        lessons.sumBy { it.size }
    }

    val completed: Boolean by lazy {
        size != 0 && studied == size
    }
}
