package dev.esnault.bunpyro.data.network.fake

import dev.esnault.bunpyro.data.network.BunproApi
import dev.esnault.bunpyro.data.network.entities.BaseRequest
import dev.esnault.bunpyro.data.network.entities.review.StudyQueue
import dev.esnault.bunpyro.data.network.entities.user.LightUserInfo
import retrofit2.HttpException
import timber.log.Timber


class FakeBunproApi(
    var user: LightUserInfo? = Mock.lightUserInfo,
    var studyQueue: StudyQueue = StudyQueue(Mock.reviewCount, null)
) : BunproApi {

    override suspend fun getUser(apiKey: String): BaseRequest<Unit> {
        Timber.d("getUser()")
        return withUserOrThrow { }
    }

    override suspend fun getStudyQueue(apiKey: String): BaseRequest<StudyQueue> {
        Timber.d("getStudyQueue()")
        return withUserOrThrow { studyQueue }
    }

    @Throws(HttpException::class)
    private fun <T> withUserOrThrow(block: () -> T): BaseRequest<T> {
        val user = user
        if (user == null) {
            throw HttpException(httpErrorResponse<BaseRequest<Unit>>(500))
        } else {
            return BaseRequest(requestedInfo = block(), userInfo = user)
        }
    }
}
