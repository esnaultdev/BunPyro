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
    abstract suspend fun getById(id: Long): FullGrammarPointDb

    @Query("""
SELECT gp.id, gp.lesson, gp.title, gp.meaning, gp.incomplete,
COUNT(review.id) AS studied FROM grammar_point AS gp
LEFT JOIN review ON review.grammar_id = gp.id AND review.type = 0
GROUP BY gp.id
""")
    abstract fun getAllOverviews(): Flow<List<GrammarPointOverviewDb>>

    suspend fun searchByTerm(term: String): List<GrammarPointOverviewDb> {
        return searchByTermImpl("\"$term\"")
    }

    @Transaction
    @Query("""
SELECT gp.id, gp.lesson, gp.title, gp.meaning, gp.incomplete,
COUNT(review.id) AS studied, gp.grammar_order FROM grammar_point AS gp
JOIN grammar_point_fts ON gp.id = grammar_point_fts.docid
AND grammar_point_fts MATCH :term
LEFT JOIN review ON review.grammar_id = gp.id AND review.type = 0
GROUP BY gp.id
ORDER BY gp.grammar_order
""")
    protected abstract suspend fun searchByTermImpl(term: String): List<GrammarPointOverviewDb>

    suspend fun searchByTermWithKana(term: String, kana: String): List<GrammarPointOverviewDb> {
        return searchByTermWithKanaImpl("\"$term\"", "\"$kana\"")
    }

    @Transaction
    @Query("""
SELECT gp.id, gp.lesson, gp.title, gp.meaning, gp.incomplete,
COUNT(review.id) AS studied, 1 AS rank, gp.grammar_order
FROM grammar_point AS gp
JOIN grammar_point_fts ON gp.id = grammar_point_fts.docid
AND grammar_point_fts.yomikata MATCH :kana
LEFT JOIN review ON review.grammar_id = gp.id AND review.type = 0
GROUP BY gp.id
UNION
SELECT gp.id, gp.lesson, gp.title, gp.meaning, gp.incomplete,
COUNT(review.id) AS studied, 2 AS rank, gp.grammar_order
FROM grammar_point AS gp
JOIN grammar_point_fts ON gp.id = grammar_point_fts.docid
AND grammar_point_fts.meaning MATCH :term
LEFT JOIN review ON review.grammar_id = gp.id AND review.type = 0
GROUP BY gp.id
ORDER BY rank, gp.grammar_order
""")
    protected abstract suspend fun searchByTermWithKanaImpl(term: String, kana: String): List<GrammarPointOverviewDb>

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
