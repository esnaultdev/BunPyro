package dev.esnault.bunpyro.data.network.fake

import dev.esnault.bunpyro.data.network.BunproApi
import dev.esnault.bunpyro.data.network.entities.BaseRequest
import dev.esnault.bunpyro.data.network.entities.review.StudyQueue
import dev.esnault.bunpyro.data.network.entities.UserInfo
import dev.esnault.bunpyro.data.network.entities.UserInfoWrapper
import retrofit2.HttpException
import timber.log.Timber


class FakeBunproApi(
    var user: UserInfo? = Mock.userInfo,
    var studyQueue: StudyQueue = StudyQueue(Mock.reviewCount)
) : BunproApi {

    override suspend fun getUser(apiKey: String): UserInfoWrapper {
        val user = user
        Timber.d("getUser()")
        if (user == null) {
            throw HttpException(httpErrorResponse<UserInfoWrapper>(500))
        } else {
            return UserInfoWrapper(user)
        }
    }

    override suspend fun getStudyQueue(apiKey: String): BaseRequest<StudyQueue> {
        Timber.d("getStudyQueue()")
        return BaseRequest(studyQueue)
    }
}
