package dev.esnault.bunpyro.data.db.grammarpoint


data class LessonProgressDb(
    val lesson: Int,
    val studied: Int,
    val total: Int
)
