package dev.esnault.bunpyro.data.repository.settings

import dev.esnault.bunpyro.domain.entities.grammar.AllGrammarFilter
import dev.esnault.bunpyro.domain.entities.settings.*
import kotlinx.coroutines.flow.Flow


interface ISettingsRepository {
    suspend fun getNightMode(): NightModeSetting

    suspend fun getFurigana(): FuriganaSetting
    suspend fun setFurigana(setting: FuriganaSetting)

    suspend fun getReviewHintLevel(): ReviewHintLevelSetting
    suspend fun setReviewHintLevel(setting: ReviewHintLevelSetting)

    suspend fun getExampleDetails(): ExampleDetailsSetting

    suspend fun setAllGrammarFilter(filter: AllGrammarFilter)
    suspend fun getAllGrammarFilter(): AllGrammarFilter

    suspend fun getHankoDisplay(): HankoDisplaySetting

    suspend fun getAudioAutoPlay(): Boolean
    suspend fun getBunnyMode(): Boolean
    suspend fun getAnkiMode(): Boolean

    // region Notifications

    suspend fun getReviewsNotificationEnabled(): Boolean
    suspend fun getReviewsNotificationThreshold(): Int
    suspend fun getReviewsNotificationRefreshRateMinutes(): Long

    // endregion

    suspend fun clearAll()

    // region Debug

    fun getDebugMocked(): Boolean

    val mockSubscription: Flow<MockSubscriptionSetting>
    fun getMockSubscription(): MockSubscriptionSetting
    fun setMockSubscription(newValue: MockSubscriptionSetting)

    // endregion
}
