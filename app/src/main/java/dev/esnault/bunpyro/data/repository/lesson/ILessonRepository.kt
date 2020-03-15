package dev.esnault.bunpyro.data.repository.lesson

import dev.esnault.bunpyro.domain.entities.JlptLesson
import dev.esnault.bunpyro.domain.entities.JlptProgress
import kotlinx.coroutines.flow.Flow


interface ILessonRepository {

    suspend fun getLessons(): Flow<List<JlptLesson>>

    fun getProgress(): Flow<JlptProgress>
}
