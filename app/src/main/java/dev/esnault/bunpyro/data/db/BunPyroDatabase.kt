package dev.esnault.bunpyro.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import dev.esnault.bunpyro.data.db.examplesentence.ExampleSentenceDao
import dev.esnault.bunpyro.data.db.examplesentence.ExampleSentenceDb
import dev.esnault.bunpyro.data.db.grammarpoint.GrammarPointDao
import dev.esnault.bunpyro.data.db.grammarpoint.GrammarPointDb
import dev.esnault.bunpyro.data.db.grammarpoint.GrammarPointFtsDb
import dev.esnault.bunpyro.data.db.search.GrammarSearchDao
import dev.esnault.bunpyro.data.db.review.ReviewDao
import dev.esnault.bunpyro.data.db.review.ReviewDb
import dev.esnault.bunpyro.data.db.reviewhistory.ReviewHistoryDao
import dev.esnault.bunpyro.data.db.reviewhistory.ReviewHistoryDb
import dev.esnault.bunpyro.data.db.supplementallink.SupplementalLinkDao
import dev.esnault.bunpyro.data.db.supplementallink.SupplementalLinkDb


@Database(
    entities = [
        GrammarPointDb::class,
        GrammarPointFtsDb::class,
        ExampleSentenceDb::class,
        SupplementalLinkDb::class,
        ReviewDb::class,
        ReviewHistoryDb::class
    ],
    version = 1
)
@TypeConverters(Converters::class)
abstract class BunPyroDatabase : RoomDatabase() {
    abstract fun grammarPointDao(): GrammarPointDao
    abstract fun grammarSearchDao(): GrammarSearchDao
    abstract fun exampleSentenceDao(): ExampleSentenceDao
    abstract fun supplementalLinkDao(): SupplementalLinkDao

    abstract fun reviewDao(): ReviewDao
    abstract fun reviewHistoryDao(): ReviewHistoryDao
}
