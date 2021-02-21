package dev.esnault.bunpyro.data.db.grammarpoint

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import dev.esnault.bunpyro.data.db.BunPyroDatabase
import dev.esnault.bunpyro.data.db.loadCustomSQLite
import dev.esnault.bunpyro.data.db.org.OrgSQLiteOpenHelperFactory
import dev.esnault.bunpyro.data.db.review.ReviewDao
import dev.esnault.bunpyro.data.db.review.ReviewDb
import dev.esnault.bunpyro.data.db.review.ReviewType
import dev.esnault.bunpyro.data.db.reviewhistory.ReviewHistoryDao
import dev.esnault.bunpyro.data.db.reviewhistory.ReviewHistoryDb
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException
import java.util.*
import kotlin.test.assertEquals

@RunWith(AndroidJUnit4::class)
class GrammarPointDaoTest {

    companion object {
        init {
            loadCustomSQLite()
        }
    }

    private lateinit var grammarPointDao: GrammarPointDao
    private lateinit var reviewDao: ReviewDao
    private lateinit var reviewHistoryDao: ReviewHistoryDao

    private lateinit var db: BunPyroDatabase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, BunPyroDatabase::class.java)
            .openHelperFactory(OrgSQLiteOpenHelperFactory())
            .build()
        grammarPointDao = db.grammarPointDao()
        reviewDao = db.reviewDao()
        reviewHistoryDao = db.reviewHistoryDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    // region Mock data

    private val grammarPointDb = GrammarPointDb(
        id = 1L,
        title = "example",
        yomikata = "example",
        meaning = "meaning",
        caution = null,
        structure = null,
        level = "N5",
        lesson = 1,
        nuance = null,
        incomplete = false,
        order = 1
    )
    private val reviewDb = ReviewDb(
        id = ReviewDb.Id(1L, ReviewType.NORMAL),
        grammarId = 1L,
        createdAt = Date(1L),
        updatedAt = Date(1L),
        nextReview = Date(1L),
        lastStudiedAt = Date(1L),
        hidden = false
    )
    private val reviewHistoryDb = ReviewHistoryDb(
        id = ReviewHistoryDb.ItemId(
            index = 0,
            reviewId = 1L,
            reviewType = ReviewType.NORMAL
        ),
        questionId = 1L,
        time = Date(1L),
        status = true,
        attempts = 1,
        streak = 1
    )

    // endregion

    // region getAllOverviews

    @Test
    fun getAllOverviewsWithHiddenReview() {
        // Given
        runBlocking {
            grammarPointDao.insertAll(listOf(grammarPointDb))
            reviewDao.insertAll(listOf(reviewDb.copy(hidden = true)))
            reviewHistoryDao.insertAll(listOf(reviewHistoryDb))
        }
        // When
        val overviews = runBlocking {
            grammarPointDao.getAllOverviews().first()
        }
        // Then
        val expected = listOf(
            GrammarPointOverviewDb(
                id = 1L,
                lesson = 1,
                title = "example",
                meaning = "meaning",
                incomplete = false,
                srsLevel = null,
                studied = false
            )
        )
        assertEquals(expected = expected, actual = overviews)
    }

    @Test
    fun getAllOverviewsWithReviewHistory() {
        // Given
        runBlocking {
            grammarPointDao.insertAll(listOf(grammarPointDb))
            reviewDao.insertAll(listOf(reviewDb))
            reviewHistoryDao.insertAll(listOf(
                ReviewHistoryDb(
                    id = ReviewHistoryDb.ItemId(
                        index = 0,
                        reviewId = 1L,
                        reviewType = ReviewType.NORMAL
                    ),
                    questionId = 1L,
                    time = Date(1L),
                    status = true,
                    attempts = 1,
                    streak = 1
                ),
                ReviewHistoryDb(
                    id = ReviewHistoryDb.ItemId(
                        index = 1,
                        reviewId = 1L,
                        reviewType = ReviewType.NORMAL
                    ),
                    questionId = 1L,
                    time = Date(2L),
                    status = true,
                    attempts = 1,
                    streak = 2
                ),
                ReviewHistoryDb(
                    id = ReviewHistoryDb.ItemId(
                        index = 2,
                        reviewId = 1L,
                        reviewType = ReviewType.NORMAL
                    ),
                    questionId = 1L,
                    time = Date(3L),
                    status = false,
                    attempts = 1,
                    streak = 1
                )
            ))
        }
        // When
        val overviews = runBlocking {
            grammarPointDao.getAllOverviews().first()
        }
        // Then
        val expected = listOf(
            GrammarPointOverviewDb(
                id = 1L,
                lesson = 1,
                title = "example",
                meaning = "meaning",
                incomplete = false,
                srsLevel = 1,
                studied = true
            )
        )
        assertEquals(expected = expected, actual = overviews)
    }

    // endregion

    // region getLessonsProgress

    // TODO

    // endregion
}
