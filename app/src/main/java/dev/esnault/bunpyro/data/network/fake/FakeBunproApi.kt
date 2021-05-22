package dev.esnault.bunpyro.data.network.fake

import dev.esnault.bunpyro.data.network.BunproApi
import dev.esnault.bunpyro.data.network.entities.BaseRequest
import dev.esnault.bunpyro.data.network.entities.review.StudyQueue
import dev.esnault.bunpyro.data.network.entities.user.LightUserInfo
import dev.esnault.bunpyro.data.network.entities.user.LightUserInfoWrapper
import retrofit2.HttpException
import timber.log.Timber


class FakeBunproApi(
    var user: LightUserInfo? = Mock.userInfo,
    var studyQueue: StudyQueue = StudyQueue(Mock.reviewCount)
) : BunproApi {

    override suspend fun getUser(apiKey: String): LightUserInfoWrapper {
        val user = user
        Timber.d("getUser()")
        if (user == null) {
            throw HttpException(httpErrorResponse<LightUserInfoWrapper>(500))
        } else {
            return LightUserInfoWrapper(user)
        }
    }

    override suspend fun getStudyQueue(apiKey: String): BaseRequest<StudyQueue> {
        Timber.d("getStudyQueue()")
        return BaseRequest(studyQueue)
    }
}
