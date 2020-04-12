package dev.esnault.bunpyro.data.repository.settings

import android.content.Context
import androidx.core.content.edit
import androidx.preference.PreferenceManager
import dev.esnault.bunpyro.data.mapper.settings.AllGrammarFilterFromStringMapper
import dev.esnault.bunpyro.data.mapper.settings.AllGrammarFilterToStringMapper
import dev.esnault.bunpyro.domain.entities.grammar.AllGrammarFilter
import dev.esnault.bunpyro.domain.entities.settings.ExampleDetailsSetting
import dev.esnault.bunpyro.domain.entities.settings.FuriganaSetting
import dev.esnault.bunpyro.domain.entities.settings.HankoDisplaySetting
import dev.esnault.bunpyro.domain.entities.settings.NightModeSetting


class SettingsRepository(context: Context) : ISettingsRepository {

    private val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

    override suspend fun getNightMode(): NightModeSetting {
        val value = sharedPreferences.getString("night_mode", "system")
        return NightModeSetting.fromValue(value)
    }

    override suspend fun getFurigana(): FuriganaSetting {
        val value = sharedPreferences.getString("furigana_default", "shown")
        return FuriganaSetting.fromValue(value)
    }

    override suspend fun setFurigana(setting: FuriganaSetting) {
        sharedPreferences.edit {
            putString("furigana_default", setting.value)
        }
    }

    override suspend fun getExampleDetails(): ExampleDetailsSetting {
        val value = sharedPreferences.getString("example_details", "shown")
        return ExampleDetailsSetting.fromValue(value)
    }

    override suspend fun getAllGrammarFilter(): AllGrammarFilter {
        val value = sharedPreferences.getString("all_grammar_filter", null)
        return AllGrammarFilterFromStringMapper().map(value)
    }

    override suspend fun setAllGrammarFilter(filter: AllGrammarFilter) {
        val value = AllGrammarFilterToStringMapper().map(filter)
        sharedPreferences.edit {
            putString("all_grammar_filter", value)
        }
    }

    override suspend fun getHankoDisplay(): HankoDisplaySetting {
        val value = sharedPreferences.getString("hanko_display", "normal")
        return HankoDisplaySetting.fromValue(value)
    }
}
