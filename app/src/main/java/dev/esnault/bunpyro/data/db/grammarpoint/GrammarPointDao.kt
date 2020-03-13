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
        val matchString = "\"$term\""
        return searchByMatch(matchString)
    }

    suspend fun searchByTermWithKana(term: String, kana: String): List<GrammarPointOverviewDb> {
        val matchString = "\"$kana\""
        return searchByMatch(matchString)
    }

    @Transaction
    @Query("""
SELECT gp.id, gp.lesson, gp.title, gp.meaning, gp.incomplete,
COUNT(review.id) AS studied FROM grammar_point AS gp
JOIN grammar_point_fts ON gp.id = grammar_point_fts.docid
AND grammar_point_fts MATCH :match
LEFT JOIN review ON review.grammar_id = gp.id AND review.type = 0
GROUP BY gp.id
""")
    protected abstract suspend fun searchByMatch(match: String): List<GrammarPointOverviewDb>

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
