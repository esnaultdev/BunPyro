package dev.esnault.bunpyro.data.db.reviewhistory

import androidx.room.*
import dev.esnault.bunpyro.data.utils.DataUpdate


private const val itemId = "history_index, review_id"

@Dao
abstract class ReviewHistoryDao {

    @Query("SELECT $itemId FROM review_history")
    abstract suspend fun getAllIds(): List<ReviewHistoryDb.ItemId>

    @Insert
    abstract suspend fun insertAll(historyItems: List<ReviewHistoryDb>)

    @Update
    abstract suspend fun updateAll(historyItems: List<ReviewHistoryDb>)

    suspend fun delete(itemId: ReviewHistoryDb.ItemId) {
        delete(itemId.index, itemId.reviewId)
    }

    @Query("DELETE FROM review_history WHERE history_index = :index AND review_id = :reviewId ")
    protected abstract suspend fun delete(index: Int, reviewId: Long)

    @Transaction
    open suspend fun performDataUpdate(
        block: (localIds: List<ReviewHistoryDb.ItemId>) -> DataUpdate<ReviewHistoryDb, ReviewHistoryDb.ItemId>
    ) {
        val dataUpdate = block(getAllIds())
        insertAll(dataUpdate.toInsert)
        updateAll(dataUpdate.toUpdate)
        dataUpdate.toDelete.forEach { toDeleteId -> delete(toDeleteId) }
    }
}
