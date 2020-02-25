package dev.esnault.bunpyro.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import dev.esnault.bunpyro.data.db.grammarpoint.GrammarPointDao
import dev.esnault.bunpyro.data.db.grammarpoint.GrammarPointDb


@Database(entities = [GrammarPointDb::class], version = 1)
abstract class BunPyroDatabase : RoomDatabase() {
    abstract fun grammarPointDao(): GrammarPointDao
}
