package dev.esnault.bunpyro.data.mapper.dbtodomain.jlpt

import dev.esnault.bunpyro.domain.entities.JLPT


fun jlptFromLesson(lesson: Int): JLPT {
    return when (lesson) {
        in 1..10 -> JLPT.N5
        in 11..20 -> JLPT.N4
        in 21..30 -> JLPT.N3
        in 31..40 -> JLPT.N2
        else -> JLPT.N1
    }
}
