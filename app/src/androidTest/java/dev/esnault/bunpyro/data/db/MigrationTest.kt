package dev.esnault.bunpyro.data.db

import android.content.Context
import androidx.room.testing.MigrationTestHelper
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import dev.esnault.bunpyro.data.db.org.OrgSQLiteOpenHelperFactory
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException


@RunWith(AndroidJUnit4::class)
class MigrationTest {

    companion object {
        init {
            loadCustomSQLite()
        }
    }

    private val targetContext: Context
        get() = InstrumentationRegistry.getInstrumentation().targetContext

    private val testDbName = "migration-test"

    private val testDbPath: String
        get() = targetContext.getDatabasePath(testDbName).path

    @get:Rule
    val helper: MigrationTestHelper = MigrationTestHelper(
        InstrumentationRegistry.getInstrumentation(),
        BunPyroDatabase::class.java.canonicalName,
        OrgSQLiteOpenHelperFactory()
    )

    @Test
    @Throws(IOException::class)
    fun migrate1To2() {
        // Given
        helper.createDatabase(testDbPath, 1).apply {
            // Insert a grammar point
            execSQL("INSERT INTO grammar_point (id, title, yomikata, meaning, caution, structure, level, lesson, nuance, incomplete, grammar_order) VALUES (42, 'title', 'yomikata', 'meaning', 'caution', 'structure', 'level', 1, 'nuance', 0, 1)")
            // Insert a normal review
            execSQL("INSERT INTO review (id, type, grammar_id, created_at, updated_at, next_review, last_studied_at) VALUES (10, 0, 42, 0, 0, 0, 0)")
            execSQL("INSERT INTO review_history (history_index, review_id, question_id, time, status, attempts, streak) VALUES (0, 10, 1, 0, 1, 1, 1)")
            // Insert a ghost review
            execSQL("INSERT INTO review (id, type, grammar_id, created_at, updated_at, next_review, last_studied_at) VALUES (11, 1, 42, 0, 0, 0, 0)")
            execSQL("INSERT INTO review_history (history_index, review_id, question_id, time, status, attempts, streak) VALUES (0, 11, 1, 0, 1, 1, 1)")
            close()
        }

        // When
        val db = helper.runMigrationsAndValidate(testDbPath, 2, true, migration_1_2)

        // Then
        // The review table should still contain two reviews
        val reviewCount = db.query("SELECT COUNT(*) FROM review").run {
            moveToFirst()
            getInt(0)
        }
        Assert.assertEquals(2, reviewCount)

        // The review history should still contain two history points
        val historyCount = db.query("SELECT COUNT(*) FROM review_history").run {
            moveToFirst()
            getInt(0)
        }
        Assert.assertEquals(2, historyCount)
    }

    @Test
    @Throws(IOException::class)
    fun migrate2To3() {
        // Given
        helper.createDatabase(testDbPath, 2).apply {
            // Insert a grammar point
            execSQL("INSERT INTO grammar_point (id, title, yomikata, meaning, caution, structure, level, lesson, nuance, incomplete, grammar_order) VALUES (42, 'title', 'yomikata', 'meaning', 'caution', 'structure', 'level', 1, 'nuance', 0, 1)")
            // Insert a normal review
            execSQL("INSERT INTO review (id, type, grammar_id, created_at, updated_at, next_review, last_studied_at) VALUES (10, 0, 42, 0, 0, 0, 0)")
            execSQL("INSERT INTO review_history (history_index, review_id, review_type, question_id, time, status, attempts, streak) VALUES (0, 10, 0, 1, 0, 1, 1, 1)")
            // Insert a ghost review
            execSQL("INSERT INTO review (id, type, grammar_id, created_at, updated_at, next_review, last_studied_at) VALUES (10, 1, 42, 0, 0, 0, 0)")
            execSQL("INSERT INTO review_history (history_index, review_id, review_type, question_id, time, status, attempts, streak) VALUES (0, 10, 1, 1, 0, 1, 1, 1)")
            close()
        }

        // When
        val db = helper.runMigrationsAndValidate(testDbPath, 3, true, migration_2_3)

        // Then
        // The review table should still contain two reviews
        val reviewCount = db.query("SELECT COUNT(*) FROM review").run {
            moveToFirst()
            getInt(0)
        }
        Assert.assertEquals(2, reviewCount)

        // The review history should still contain two history points
        val historyCount = db.query("SELECT COUNT(*) FROM review_history").run {
            moveToFirst()
            getInt(0)
        }
        Assert.assertEquals(2, historyCount)
    }

    @Test
    @Throws(IOException::class)
    fun migrate3To4() {
        // Given
        helper.createDatabase(testDbPath, 3).apply {
            // Insert a grammar point
            execSQL("INSERT INTO grammar_point (id, title, yomikata, meaning, caution, structure, level, lesson, nuance, incomplete, grammar_order) VALUES (42, 'title', 'yomikata', 'meaning', 'caution', 'structure', 'level', 1, 'nuance', 0, 1)")
            // Insert a normal review
            execSQL("INSERT INTO review (id, type, id_type, grammar_id, created_at, updated_at, next_review, last_studied_at) VALUES (10, 0, '10_0', 42, 0, 0, 0, 0)")
            execSQL("INSERT INTO review_history (history_index, review_id, review_type, review_id_type, question_id, time, status, attempts, streak) VALUES (0, 10, 0, '10_0', 1, 0, 1, 1, 1)")
            // Insert a ghost review
            execSQL("INSERT INTO review (id, type, id_type, grammar_id, created_at, updated_at, next_review, last_studied_at) VALUES (10, 1, '10_1', 42, 0, 0, 0, 0)")
            execSQL("INSERT INTO review_history (history_index, review_id, review_type, review_id_type, question_id, time, status, attempts, streak) VALUES (0, 10, 1, '10_1', 1, 0, 1, 1, 1)")
            close()
        }

        // When
        val db = helper.runMigrationsAndValidate(testDbPath, 4, true, migration_3_4)

        // Then
        // The review table should still contain two reviews
        val reviewCount = db.query("SELECT COUNT(*) FROM review WHERE hidden = 0").run {
            moveToFirst()
            getInt(0)
        }
        Assert.assertEquals(2, reviewCount)

        // The review history should still contain two history points
        val historyCount = db.query("SELECT COUNT(*) FROM review_history").run {
            moveToFirst()
            getInt(0)
        }
        Assert.assertEquals(2, historyCount)
    }
}
