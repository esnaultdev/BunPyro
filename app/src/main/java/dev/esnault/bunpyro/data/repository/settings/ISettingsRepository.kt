package dev.esnault.bunpyro.data.repository.settings

import dev.esnault.bunpyro.domain.entities.grammar.AllGrammarFilter
import dev.esnault.bunpyro.domain.entities.settings.*


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

    suspend fun clearAll()

    fun getDebugMocked(): Boolean
}
