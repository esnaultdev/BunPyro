package dev.esnault.bunpyro.data.db.supplementallink

import androidx.room.*
import dev.esnault.bunpyro.data.utils.DataUpdate


@Dao
abstract class SupplementalLinkDao {

    @Query("SELECT id FROM supplemental_link")
    abstract suspend fun getAllIds(): List<Long>

    @Query("SELECT id, grammar_id FROM supplemental_link")
    abstract suspend fun getAllFilterIds(): List<SupplementalLinkDb.FilterId>

    @Insert
    abstract suspend fun insertAll(links: List<SupplementalLinkDb>)

    @Update
    abstract suspend fun updateAll(links: List<SupplementalLinkDb>)

    @Query("DELETE FROM supplemental_link WHERE id IN (:ids)")
    abstract suspend fun deleteAll(ids: List<Long>)

    @Transaction
    open suspend fun performDataUpdate(
        block: (localIds: List<Long>) -> DataUpdate<SupplementalLinkDb, Long>
    ) {
        val dataUpdate = block(getAllIds())
        insertAll(dataUpdate.toInsert)
        updateAll(dataUpdate.toUpdate)
        deleteAll(dataUpdate.toDelete)
    }

    @Transaction
    open suspend fun performPartialDataUpdate(
        block: (localIds: List<SupplementalLinkDb.FilterId>) -> DataUpdate<SupplementalLinkDb, Long>
    ) {
        val dataUpdate = block(getAllFilterIds())
        insertAll(dataUpdate.toInsert)
        updateAll(dataUpdate.toUpdate)
        deleteAll(dataUpdate.toDelete)
    }
}
