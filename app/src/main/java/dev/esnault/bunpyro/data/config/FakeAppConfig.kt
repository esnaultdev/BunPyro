package dev.esnault.bunpyro.data.config

import dev.esnault.bunpyro.BuildConfig
import dev.esnault.bunpyro.domain.entities.user.UserSubscription
import java.util.*


class FakeAppConfig(
    var migrationVersionCode: Int? = BuildConfig.VERSION_CODE,
    var apiKey: String? = null,
    var firstSyncCompleted: Boolean = false,
    var grammarPointsEtag: String? = null,
    var exampleSentencesEtag: String? = null,
    var supplementalLinksEtag: String? = null,
    var reviewsEtag: String? = null,
    var studyQueueCount: Int? = null,
    var nextReviewDate: Date? = null,
    var username: String? = null,
    var userSubscription: UserSubscription = UserSubscription.DEFAULT
) : IAppConfig {

    override suspend fun getMigrationVersionCode(): Int? = migrationVersionCode

    override suspend fun saveMigrationVersionCode(versionCode: Int) {
        migrationVersionCode = versionCode
    }

    override suspend fun getApiKey(): String? = apiKey

    override suspend fun setApiKey(apiKey: String?) {
        this.apiKey = apiKey
    }

    override suspend fun getFirstSyncCompleted(): Boolean = firstSyncCompleted

    override suspend fun saveFirstSyncCompleted(completed: Boolean) {
        firstSyncCompleted = completed
    }

    override suspend fun getGrammarPointsEtag(): String? = grammarPointsEtag

    override suspend fun saveGrammarPointsEtag(eTag: String?) {
        grammarPointsEtag = eTag
    }

    override suspend fun getExampleSentencesEtag(): String? = exampleSentencesEtag

    override suspend fun saveExampleSentencesEtag(eTag: String?) {
        exampleSentencesEtag = eTag
    }

    override suspend fun saveSupplementalLinksEtag(eTag: String?) {
        supplementalLinksEtag = eTag
    }

    override suspend fun getSupplementalLinksEtag(): String? = supplementalLinksEtag

    override suspend fun saveReviewsEtag(eTag: String?) {
        reviewsEtag = eTag
    }

    override suspend fun getReviewsEtag(): String? = reviewsEtag

    override suspend fun getStudyQueueCount(): Int? = studyQueueCount

    override suspend fun setStudyQueueCount(count: Int?) {
        studyQueueCount = count
    }

    override suspend fun getNextReviewDate(): Date? = nextReviewDate

    override suspend fun setNextReviewDate(date: Date?) {
        nextReviewDate = date
    }

    override suspend fun getUserName(): String? = username

    override suspend fun setUserName(name: String?) {
        username = name
    }

    override suspend fun getSubscription(): UserSubscription = userSubscription

    override suspend fun setSubscription(subscription: UserSubscription) {
        userSubscription = subscription
    }
}
