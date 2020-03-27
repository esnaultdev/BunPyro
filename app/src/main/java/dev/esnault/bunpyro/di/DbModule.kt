package dev.esnault.bunpyro.di

import androidx.room.Room
import dev.esnault.bunpyro.data.db.BunPyroDatabase
import dev.esnault.bunpyro.data.db.bunPyroDbMigrations
import dev.esnault.bunpyro.data.db.examplesentence.ExampleSentenceDao
import dev.esnault.bunpyro.data.db.grammarpoint.GrammarPointDao
import dev.esnault.bunpyro.data.db.search.GrammarSearchDao
import dev.esnault.bunpyro.data.db.org.OrgSQLiteOpenHelperFactory
import dev.esnault.bunpyro.data.db.review.ReviewDao
import dev.esnault.bunpyro.data.db.reviewhistory.ReviewHistoryDao
import dev.esnault.bunpyro.data.db.supplementallink.SupplementalLinkDao
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module


/**
 * Module providing the real DB.
 */
@Suppress("RemoveExplicitTypeArguments")
val dbModule = module {
    single<BunPyroDatabase> {
        val appContext = androidApplication()
        val path = appContext.getDatabasePath("bunpyro.db").path

        Room.databaseBuilder(
                androidApplication(),
                BunPyroDatabase::class.java,
                path
            )
            .openHelperFactory(OrgSQLiteOpenHelperFactory())
            .addMigrations(*bunPyroDbMigrations)
            .build()
    }
}


/**
 * Module providing an in memory database.
 */
@Suppress("RemoveExplicitTypeArguments")
val fakeDbModule = module {
    single<BunPyroDatabase> {
        Room.inMemoryDatabaseBuilder(androidApplication(), BunPyroDatabase::class.java)
            .openHelperFactory(OrgSQLiteOpenHelperFactory())
            .addMigrations(*bunPyroDbMigrations)
            .build()
    }
}


/**
 * Module providing the DAOs of the provided database.
 */
@Suppress("RemoveExplicitTypeArguments")
val daoModule = module {
    factory<GrammarPointDao> {
        val db: BunPyroDatabase = get()
        db.grammarPointDao()
    }

    factory<GrammarSearchDao> {
        val db: BunPyroDatabase = get()
        db.grammarSearchDao()
    }

    factory<ExampleSentenceDao> {
        val db: BunPyroDatabase = get()
        db.exampleSentenceDao()
    }

    factory<SupplementalLinkDao> {
        val db: BunPyroDatabase = get()
        db.supplementalLinkDao()
    }

    factory<ReviewDao> {
        val db: BunPyroDatabase = get()
        db.reviewDao()
    }

    factory<ReviewHistoryDao> {
        val db: BunPyroDatabase = get()
        db.reviewHistoryDao()
    }
}
