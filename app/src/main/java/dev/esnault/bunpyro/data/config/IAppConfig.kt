package dev.esnault.bunpyro.data.config

import dev.esnault.bunpyro.domain.entities.user.UserSubscription
import java.util.*


interface IAppConfig {

    // region

    suspend fun getMigrationVersionCode(): Int?
    suspend fun saveMigrationVersionCode(versionCode: Int)

    // endregion

    // region Sync

    suspend fun getFirstSyncCompleted(): Boolean
    suspend fun saveFirstSyncCompleted(completed: Boolean)

    suspend fun getGrammarPointsEtag(): String?
    suspend fun saveGrammarPointsEtag(eTag: String?)

    suspend fun getExampleSentencesEtag(): String?
    suspend fun saveExampleSentencesEtag(eTag: String?)

    suspend fun saveSupplementalLinksEtag(eTag: String?)
    suspend fun getSupplementalLinksEtag(): String?

    suspend fun saveReviewsEtag(eTag: String?)
    suspend fun getReviewsEtag(): String?

    // endregion

    // region User

    suspend fun getApiKey(): String?
    suspend fun setApiKey(apiKey: String?)

    suspend fun getStudyQueueCount(): Int?
    suspend fun setStudyQueueCount(count: Int?)
    suspend fun getNextReviewDate(): Date?
    suspend fun setNextReviewDate(date: Date?)

    suspend fun getUserName(): String?
    suspend fun setUserName(name: String?)

    suspend fun getSubscription(): UserSubscription
    suspend fun setSubscription(subscription: UserSubscription)

    // endregion
}
