package dev.esnault.bunpyro.data.db.grammarpoint

import androidx.room.*
import dev.esnault.bunpyro.data.utils.DataUpdate
import kotlinx.coroutines.flow.Flow


@Dao
abstract class GrammarPointDao {

    @Query("SELECT * FROM grammar_point ORDER BY grammar_order")
    abstract suspend fun getAll(): List<GrammarPointDb>

    @Query("SELECT id FROM grammar_point")
    abstract suspend fun getAllIds(): List<Int>

    @Transaction
    @Query("SELECT * FROM grammar_point WHERE id = :id")
    abstract suspend fun getById(id: Int): FullGrammarPointDb

    @Query("SELECT id, lesson, title, meaning, incomplete FROM grammar_point")
    abstract fun getAllOverviews(): Flow<List<GrammarPointOverviewDb>>

    @Insert
    abstract suspend fun insertAll(users: List<GrammarPointDb>)

    @Update
    abstract suspend fun updateAll(users: List<GrammarPointDb>)

    @Query("DELETE FROM grammar_point WHERE id IN (:ids)")
    abstract suspend fun deleteAll(ids: List<Int>)

    @Transaction
    open suspend fun performDataUpdate(
        block: (localIds: List<Int>) -> DataUpdate<GrammarPointDb, Int>
    ) {
        val dataUpdate = block(getAllIds())
        insertAll(dataUpdate.toInsert)
        updateAll(dataUpdate.toUpdate)
        deleteAll(dataUpdate.toDelete)
    }
}
