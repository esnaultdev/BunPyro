package dev.esnault.bunpyro.data.repository.lesson

import dev.esnault.bunpyro.domain.entities.JlptLesson
import kotlinx.coroutines.flow.Flow


interface ILessonRepository {

    fun getLessons(): Flow<List<JlptLesson>>
}
