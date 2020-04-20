package dev.esnault.bunpyro.data.db.review

import androidx.room.*
import dev.esnault.bunpyro.data.utils.DataUpdate


@Dao
abstract class ReviewDao {

    @Query("SELECT id, type FROM review")
    abstract suspend fun getAllIds(): List<ReviewDb.Id>

    @Query("SELECT * FROM review WHERE id = :id AND type = :type")
    protected abstract suspend fun get(id: Long, type: ReviewType): ReviewDb

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
}
