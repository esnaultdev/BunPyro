package dev.esnault.bunpyro.data.network.entities

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import dev.esnault.bunpyro.KoinTestRule
import dev.esnault.bunpyro.data.network.adapter.BunProDateAdapter
import dev.esnault.bunpyro.di.networkModule
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.koin.test.KoinTest
import org.koin.test.inject
import java.util.*


class BunProDateJsonTest : KoinTest {

    val moshi: Moshi by inject()

    @get:Rule
    val koinTestRule = KoinTestRule.create {
        modules(networkModule)
    }

    @Test
    fun `Parsing a BunPro date should return the correct date`() {
        // Given
        val input = "{\"date\": \"2019-03-31 21:00:00 +0000\"}"
        val adapter: JsonAdapter<BunProDateWrapper> = moshi.adapter(BunProDateWrapper::class.java)

        // When
        val output = adapter.fromJson(input)?.date

        // Then
        val expected = BunProDate(Date(1554066000000L))
        Assert.assertEquals(expected, output)
    }

    private data class BunProDateWrapper(val date: BunProDate)
}

class BunProDateTest : KoinTest {

    @Test
    fun `Parsing a BunPro date should return the correct date`() {
        // Given
        val input = "2019-03-31 21:00:00 +0000"
        // When
        val output = BunProDateAdapter.parse(input)
        // Then
        val expected = BunProDate(Date(1554066000000L))
        Assert.assertEquals(expected, output)
    }
}
