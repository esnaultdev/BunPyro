package dev.esnault.bunpyro.data.db.grammarpoint

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow


@Dao
interface GrammarPointDao {

    @Query("SELECT * FROM grammar_point ORDER BY grammar_order")
    suspend fun getAll(): List<GrammarPointDb>

    @Query("SELECT * FROM grammar_point WHERE id = :id")
    suspend fun getById(id: Int): GrammarPointDb

    @Query("SELECT id, lesson, title, meaning FROM grammar_point")
    fun getAllOverviews(): Flow<List<GrammarPointOverviewDb>>

    @Insert
    suspend fun insertAll(users: List<GrammarPointDb>)
}
