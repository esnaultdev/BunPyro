package dev.esnault.bunpyro.data.repository.settings

import dev.esnault.bunpyro.domain.entities.grammar.AllGrammarFilter
import dev.esnault.bunpyro.domain.entities.settings.FuriganaSetting
import dev.esnault.bunpyro.domain.entities.settings.NightModeSetting


interface ISettingsRepository {
    suspend fun getNightMode(): NightModeSetting

    suspend fun getFurigana(): FuriganaSetting
    suspend fun setFurigana(setting: FuriganaSetting)

    suspend fun setAllGrammarFilter(filter: AllGrammarFilter)
    suspend fun getAllGrammarFilter(): AllGrammarFilter
}
