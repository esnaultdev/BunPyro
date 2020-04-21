package dev.esnault.bunpyro.data.db.grammarpoint

import androidx.room.*
import dev.esnault.bunpyro.data.utils.DataUpdate
import kotlinx.coroutines.flow.Flow


@Dao
abstract class GrammarPointDao {

    @Query("SELECT * FROM grammar_point ORDER BY grammar_order")
    abstract suspend fun getAll(): List<GrammarPointDb>

    @Query("SELECT id FROM grammar_point")
    abstract suspend fun getAllIds(): List<Long>

    @Transaction
    @Query("SELECT * FROM grammar_point WHERE id = :id")
    abstract fun getById(id: Long): Flow<FullGrammarPointDb>

    @Query("""
SELECT gp.id, gp.lesson, gp.title, gp.meaning, gp.incomplete,
MAX(rv_h.streak) AS srsLevel, COUNT(rv.id) AS studied FROM grammar_point AS gp
LEFT JOIN review AS rv ON rv.grammar_id = gp.id AND rv.type = 0 AND rv.hidden = 0
LEFT JOIN review_history AS rv_h ON rv_h.review_id == rv.id AND rv_h.review_type = 0
GROUP BY gp.id
""")
    abstract fun getAllOverviews(): Flow<List<GrammarPointOverviewDb>>

    @Query("""
SELECT lesson, COUNT(NULLIF(gp_studied, 0)) AS studied, COUNT(gp_studied) AS total FROM
(SELECT gp.lesson, COUNT(review.id) AS gp_studied FROM grammar_point AS gp
LEFT JOIN review ON review.grammar_id = gp.id AND review.type = 0
WHERE gp.incomplete = 0
GROUP BY gp.id)
GROUP BY lesson
""")
    abstract fun getLessonsProgress(): Flow<List<LessonProgressDb>>

    @Insert
    abstract suspend fun insertAll(users: List<GrammarPointDb>)

    @Update
    abstract suspend fun updateAll(users: List<GrammarPointDb>)

    @Query("DELETE FROM grammar_point WHERE id IN (:ids)")
    abstract suspend fun deleteAll(ids: List<Long>)

    @Transaction
    open suspend fun performDataUpdate(
        block: (localIds: List<Long>) -> DataUpdate<GrammarPointDb, Long>
    ) {
        val dataUpdate = block(getAllIds())
        insertAll(dataUpdate.toInsert)
        updateAll(dataUpdate.toUpdate)
        deleteAll(dataUpdate.toDelete)
    }
}
