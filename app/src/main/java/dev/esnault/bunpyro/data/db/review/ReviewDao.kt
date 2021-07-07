package dev.esnault.bunpyro.data.db.review

import androidx.room.*
import dev.esnault.bunpyro.data.utils.DataUpdate
import kotlinx.coroutines.flow.Flow


@Dao
abstract class ReviewDao {

    @Query("SELECT id, type FROM review")
    abstract suspend fun getAllIds(): List<ReviewDb.Id>

    @Query("SELECT id, type, grammar_id FROM review")
    abstract suspend fun getAllFilterIds(): List<ReviewDb.FilterId>

    @Query("SELECT * FROM review WHERE id = :id AND type = :type")
    protected abstract suspend fun get(id: Long, type: ReviewType): ReviewDb

    // Ghost reviews are considered done after a streak of 4.
    @Query("""
SELECT COUNT(rv.id)
FROM review AS rv
LEFT JOIN (
    /* This sub query selects the last review history row for each ghost review. */
    /* Inspired by https://stackoverflow.com/a/123481/9198676 */
    SELECT rv_h1.*
    FROM review_history AS rv_h1
    LEFT JOIN review_history AS rv_h2
    ON (rv_h1.review_id = rv_h2.review_id AND rv_h1.review_type = rv_h2.review_type AND rv_h1.history_index < rv_h2.history_index)
    WHERE rv_h2.review_id IS NULL AND rv_h1.review_type = 1
) AS rv_h ON rv.id = rv_h.review_id
WHERE rv.type = 1 AND rv.hidden = 0 AND (rv_h.streak IS NULL OR rv_h.streak < 4)
""")
    abstract fun getGhostReviewsCount(): Flow<Int>

    @Insert
    abstract suspend fun insertAll(reviews: List<ReviewDb>)

    @Update
    abstract suspend fun updateAll(reviews: List<ReviewDb>)

    @Query("UPDATE review SET hidden = :hidden WHERE id = :id AND type = :type ")
    protected abstract suspend fun updateHidden(id: Long, type: ReviewType, hidden: Boolean)

    suspend fun updateHidden(id: ReviewDb.Id, hidden: Boolean) {
        updateHidden(id.id, id.type, hidden)
    }

    suspend fun deleteAll(ids: List<ReviewDb.Id>) {
        ids.forEach { delete(it.id, it.type) }
    }

    suspend fun delete(itemId: ReviewDb.Id) {
        delete(itemId.id, itemId.type)
    }

    @Query("DELETE FROM review WHERE id = :id AND type = :type ")
    protected abstract suspend fun delete(id: Long, type: ReviewType)

    @Transaction
    open suspend fun performDataUpdate(
        block: (localIds: List<ReviewDb.Id>) -> DataUpdate<ReviewDb, ReviewDb.Id>
    ) {
        val dataUpdate = block(getAllIds())
        insertAll(dataUpdate.toInsert)
        updateAll(dataUpdate.toUpdate)
        deleteAll(dataUpdate.toDelete)
    }

    @Transaction
    open suspend fun performPartialDataUpdate(
        block: (localIds: List<ReviewDb.FilterId>) -> DataUpdate<ReviewDb, ReviewDb.Id>
    ) {
        val dataUpdate = block(getAllFilterIds())
        insertAll(dataUpdate.toInsert)
        updateAll(dataUpdate.toUpdate)
        deleteAll(dataUpdate.toDelete)
    }

    @Query("DELETE FROM review")
    abstract suspend fun deleteAll()
}
