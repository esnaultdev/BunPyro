package dev.esnault.bunpyro.data.network.entities.user

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import dev.esnault.bunpyro.KoinTestRule
import dev.esnault.bunpyro.data.network.entities.BaseRequest
import dev.esnault.bunpyro.di.networkModule
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.koin.test.KoinTest
import org.koin.test.inject


class LightUserInfoTest : KoinTest {

    val moshi: Moshi by inject()

    @get:Rule
    val koinTestRule = KoinTestRule.create {
        modules(networkModule)
    }

    @Test
    fun parseUserInfo() {
        // Given
        val input = userInfo
        val userInfoType = Types.newParameterizedType(BaseRequest::class.java, Unit::class.java)
        val adapter: JsonAdapter<BaseRequest<Unit>> = moshi.adapter(userInfoType)

        // When
        val output = adapter.fromJson(input)

        // Then
        val expected = BaseRequest(
            requestedInfo = Unit,
            userInfo = LightUserInfo(
                userName = "matthieuesnault",
                grammarPointCount = 219,
                ghostReviewCount = 25,
                creationDate = 1528066276L
            )
        )
        assertEquals(expected, output)
    }
}

private const val userInfo = """
{
  "user_information": {
    "username": "matthieuesnault",
    "grammar_point_count": 219,
    "ghost_review_count": 25,
    "creation_date": 1528066276
  },
  "requested_information": null
}
"""
