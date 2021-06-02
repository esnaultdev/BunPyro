package dev.esnault.bunpyro.data.network.entities.user

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import dev.esnault.bunpyro.KoinTestRule
import dev.esnault.bunpyro.di.networkModule
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.koin.test.KoinTest
import org.koin.test.inject


class FullUserInfoTest : KoinTest {

    val moshi: Moshi by inject()

    @get:Rule
    val koinTestRule = KoinTestRule.create {
        modules(networkModule)
    }

    @Test
    fun parseUserInfo() {
        // Given
        val input = userInfo
        val adapter: JsonAdapter<FullUserInfo> = moshi.adapter(FullUserInfo::class.java)

        // When
        val output = adapter.fromJson(input)

        // Then
        val expected = FullUserInfo(
            id = 12345,
            userName = "matthieuesnault",
            subscriber = true
        )
        Assert.assertEquals(expected, output)
    }
}

private const val userInfo = """
{  
   "id":12345,
   "hide_english":"No",
   "furigana":"On",
   "username":"matthieuesnault",
   "light_mode":"Off",
   "bunny_mode":"Off",
   "new_reviews":[  

   ],
   "review_english":"Show",
   "subscriber":true
}
"""
