package dev.esnault.bunpyro.data.db.review

import androidx.room.*
import dev.esnault.bunpyro.data.utils.DataUpdate


@Dao
abstract class ReviewDao {

    @Query("SELECT id FROM review")
    abstract suspend fun getAllIds(): List<Long>

    @Insert
    abstract suspend fun insertAll(reviews: List<ReviewDb>)

    @Update
    abstract suspend fun updateAll(reviews: List<ReviewDb>)

    @Query("DELETE FROM review WHERE id IN (:ids)")
    abstract suspend fun deleteAll(ids: List<Long>)

    @Transaction
    open suspend fun performDataUpdate(
        block: (localIds: List<Long>) -> DataUpdate<ReviewDb, Long>
    ) {
        val dataUpdate = block(getAllIds())
        insertAll(dataUpdate.toInsert)
        updateAll(dataUpdate.toUpdate)
        deleteAll(dataUpdate.toDelete)
    }
}
