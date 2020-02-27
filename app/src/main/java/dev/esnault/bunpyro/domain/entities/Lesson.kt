package dev.esnault.bunpyro.domain.entities


data class Lesson(
    val id: Int,
    val points: List<GrammarPointOverview>
) {
    val studied: Int by lazy {
        points.count { it.studied }
    }

    val size: Int
        get() = points.size

    val completed: Boolean by lazy {
        size != 0 && studied == size
    }
}
