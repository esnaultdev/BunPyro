package dev.esnault.bunpyro.data.db.review

import androidx.room.*
import dev.esnault.bunpyro.data.utils.DataUpdate


@Dao
abstract class ReviewDao {

    @Query("SELECT id, type FROM review")
    abstract suspend fun getAllIds(): List<ReviewDb.Id>

    @Insert
    abstract suspend fun insertAll(reviews: List<ReviewDb>)

    @Update
    abstract suspend fun updateAll(reviews: List<ReviewDb>)

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
}
