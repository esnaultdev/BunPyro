package dev.esnault.bunpyro.data.network.fake

import dev.esnault.bunpyro.data.network.BunproApi
import dev.esnault.bunpyro.data.network.entities.UserInfo
import dev.esnault.bunpyro.data.network.entities.UserInfoWrapper
import retrofit2.HttpException


class FakeBunproApi(var user: UserInfo? = Mock.userInfo) : BunproApi {

    override suspend fun getUser(apiKey: String): UserInfoWrapper {
        val user = user
        if (user == null) {
            throw HttpException(httpErrorResponse<UserInfoWrapper>(500))
        } else {
            return UserInfoWrapper(user)
        }
    }
}
