package dev.esnault.bunpyro.data.db.examplesentence

import androidx.room.*
import dev.esnault.bunpyro.data.utils.DataUpdate


@Dao
abstract class ExampleSentenceDao {

    @Query("SELECT id FROM example_sentence")
    abstract suspend fun getAllIds(): List<Long>

    @Query("SELECT id, grammar_id FROM example_sentence")
    abstract suspend fun getAllFilterIds(): List<ExampleSentenceDb.FilterId>

    @Insert
    abstract suspend fun insertAll(sentences: List<ExampleSentenceDb>)

    @Update
    abstract suspend fun updateAll(sentences: List<ExampleSentenceDb>)

    @Query("DELETE FROM example_sentence WHERE id IN (:ids)")
    abstract suspend fun deleteAll(ids: List<Long>)

    @Transaction
    open suspend fun performDataUpdate(
        block: (localIds: List<Long>) -> DataUpdate<ExampleSentenceDb, Long>
    ) {
        val dataUpdate = block(getAllIds())
        insertAll(dataUpdate.toInsert)
        updateAll(dataUpdate.toUpdate)
        deleteAll(dataUpdate.toDelete)
    }

    @Transaction
    open suspend fun performPartialDataUpdate(
        block: (localIds: List<ExampleSentenceDb.FilterId>) -> DataUpdate<ExampleSentenceDb, Long>
    ) {
        val dataUpdate = block(getAllFilterIds())
        insertAll(dataUpdate.toInsert)
        updateAll(dataUpdate.toUpdate)
        deleteAll(dataUpdate.toDelete)
    }
}
