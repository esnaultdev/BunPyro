package dev.esnault.bunpyro.data.db

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase


private val migration_1_to_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("CREATE VIRTUAL TABLE IF NOT EXISTS `grammar_point_fts` USING FTS4(`title`, `yomikata`, `meaning`, content=`grammar_point`)")
        database.execSQL("INSERT INTO grammar_point_fts(grammar_point_fts) VALUES ('rebuild')")
    }
}

val bunPyroDbMigrations = arrayOf<Migration>(
    migration_1_to_2
)
