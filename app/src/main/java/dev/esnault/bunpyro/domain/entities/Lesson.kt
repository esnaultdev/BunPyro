package dev.esnault.bunpyro.domain.entities

import dev.esnault.bunpyro.domain.entities.grammar.GrammarPointOverview
import dev.esnault.bunpyro.domain.utils.lazyNone


data class Lesson(
    val id: Int,
    val points: List<GrammarPointOverview>
) {
    val studied: Int by lazyNone {
        points.count { it.studied }
    }

    val size: Int
        get() = points.size

    /** Lesson number in the JLPT lessons */
    val number: Int by lazyNone {
        ((id - 1) % 10) + 1
    }

    val completed: Boolean by lazyNone {
        size != 0 && studied == size
    }
}
