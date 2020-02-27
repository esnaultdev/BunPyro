package dev.esnault.bunpyro.data.repository.lesson

import dev.esnault.bunpyro.data.db.grammarpoint.GrammarPointDao
import dev.esnault.bunpyro.data.mapper.dbtodomain.JlptLessonMapper
import dev.esnault.bunpyro.domain.entities.JlptLesson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


class LessonRepository(
    private val grammarPointDao: GrammarPointDao
) : ILessonRepository {

    override fun getLessons(): Flow<List<JlptLesson>> {
        val mapper = JlptLessonMapper()
        return grammarPointDao.getAllOverviews()
            .map { mapper.map(it) }
    }
}
