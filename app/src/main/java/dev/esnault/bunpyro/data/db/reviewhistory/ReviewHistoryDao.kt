package dev.esnault.bunpyro.data.db.reviewhistory

import androidx.room.*
import dev.esnault.bunpyro.data.db.review.ReviewDb
import dev.esnault.bunpyro.data.db.review.ReviewType
import dev.esnault.bunpyro.data.utils.DataUpdate


private const val itemId = "history_index, review_id, review_type"

@Dao
abstract class ReviewHistoryDao {

    @Query("SELECT $itemId FROM review_history")
    abstract suspend fun getAllIds(): List<ReviewHistoryDb.ItemId>

    @Query("SELECT * FROM review_history WHERE review_id = :reviewId AND review_type = :reviewType ORDER BY history_index")
    abstract suspend fun getReviewHistory(reviewId: Long, reviewType: ReviewType): List<ReviewHistoryDb>

    suspend fun getReviewHistory(reviewId: ReviewDb.Id): List<ReviewHistoryDb> {
        return getReviewHistory(reviewId.id, reviewId.type)
    }

    @Insert
    abstract suspend fun insertAll(historyItems: List<ReviewHistoryDb>)

    @Insert
    abstract suspend fun insert(historyItem: ReviewHistoryDb)

    @Update
    abstract suspend fun updateAll(historyItems: List<ReviewHistoryDb>)

    suspend fun delete(itemId: ReviewHistoryDb.ItemId) {
        delete(itemId.index, itemId.reviewId, itemId.reviewType)
    }

    @Query("DELETE FROM review_history WHERE history_index = :index AND review_id = :reviewId AND review_type = :reviewType")
    protected abstract suspend fun delete(index: Int, reviewId: Long, reviewType: ReviewType)

    suspend fun deleteHistoryForReview(reviewId: ReviewDb.Id) {
        deleteHistoryForReview(reviewId.id, reviewId.type)
    }

    @Query("DELETE FROM review_history WHERE review_id = :reviewId AND review_type = :reviewType")
    protected abstract suspend fun deleteHistoryForReview(reviewId: Long, reviewType: ReviewType)

    @Transaction
    open suspend fun performDataUpdate(
        block: (localIds: List<ReviewHistoryDb.ItemId>) -> DataUpdate<ReviewHistoryDb, ReviewHistoryDb.ItemId>
    ) {
        val dataUpdate = block(getAllIds())
        insertAll(dataUpdate.toInsert)
        updateAll(dataUpdate.toUpdate)
        dataUpdate.toDelete.forEach { toDeleteId -> delete(toDeleteId) }
    }

    @Query("DELETE FROM review_history")
    abstract suspend fun deleteAll()
}
