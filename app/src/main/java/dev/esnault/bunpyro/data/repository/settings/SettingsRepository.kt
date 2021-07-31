package dev.esnault.bunpyro.data.repository.settings

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.edit
import androidx.preference.PreferenceManager
import dev.esnault.bunpyro.android.res.toNightMode
import dev.esnault.bunpyro.data.mapper.settings.AllGrammarFilterFromStringMapper
import dev.esnault.bunpyro.data.mapper.settings.AllGrammarFilterToStringMapper
import dev.esnault.bunpyro.domain.entities.grammar.AllGrammarFilter
import dev.esnault.bunpyro.domain.entities.settings.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.withContext


class SettingsRepository(context: Context) : ISettingsRepository {

    private val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

    private object Key {
        const val NIGHT_MODE = "night_mode"
        const val FURIGANA = "furigana_default"
        const val REVIEW_HINT_LEVEL = "review_hint_level"
        const val EXAMPLE_DETAILS = "example_details"
        const val GRAMMAR_FILTERS = "all_grammar_filter"
        const val HANKO_DISPLAY = "hanko_display"
        const val TEXT_ANIMATION = "text_animation"

        val allKeys: List<String>
            get() = listOf(
                NIGHT_MODE, FURIGANA, REVIEW_HINT_LEVEL, EXAMPLE_DETAILS, GRAMMAR_FILTERS,
                HANKO_DISPLAY
            )
    }

    private val _mockSubscription = MutableSharedFlow<MockSubscriptionSetting>(replay = 1)
    override val mockSubscription: Flow<MockSubscriptionSetting> = _mockSubscription.asSharedFlow()

    init {
        _mockSubscription.tryEmit(getMockSubscription())
    }

    override suspend fun getNightMode(): NightModeSetting {
        val value = sharedPreferences.getString(Key.NIGHT_MODE, "system")
        return NightModeSetting.fromValue(value)
    }

    override suspend fun getFurigana(): FuriganaSetting {
        val value = sharedPreferences.getString(Key.FURIGANA, "shown")
        return FuriganaSetting.fromValue(value)
    }

    override suspend fun setFurigana(setting: FuriganaSetting) {
        sharedPreferences.edit { putString(Key.FURIGANA, setting.value) }
    }

    override suspend fun getTextAnimationEnabled(): Boolean {
        return sharedPreferences.getBoolean(Key.TEXT_ANIMATION, true)
    }

    override suspend fun getReviewHintLevel(): ReviewHintLevelSetting {
        val value = sharedPreferences.getString(Key.REVIEW_HINT_LEVEL, "shown")
        return ReviewHintLevelSetting.fromValue(value)
    }

    override suspend fun setReviewHintLevel(setting: ReviewHintLevelSetting) {
        sharedPreferences.edit {
            putString(Key.REVIEW_HINT_LEVEL, setting.value)
        }
    }

    override suspend fun getExampleDetails(): ExampleDetailsSetting {
        val value = sharedPreferences.getString(Key.EXAMPLE_DETAILS, "shown")
        return ExampleDetailsSetting.fromValue(value)
    }

    override suspend fun getAllGrammarFilter(): AllGrammarFilter {
        val value = sharedPreferences.getString(Key.GRAMMAR_FILTERS, null)
        return AllGrammarFilterFromStringMapper().map(value)
    }

    override suspend fun setAllGrammarFilter(filter: AllGrammarFilter) {
        val value = AllGrammarFilterToStringMapper().map(filter)
        sharedPreferences.edit {
            putString(Key.GRAMMAR_FILTERS, value)
        }
    }

    override suspend fun getHankoDisplay(): HankoDisplaySetting {
        val value = sharedPreferences.getString(Key.HANKO_DISPLAY, "normal")
        return HankoDisplaySetting.fromValue(value)
    }

    override suspend fun clearAll() {
        // Not using the clear function of the shared preferences since the default preferences file
        // is shared with other classes.
        sharedPreferences.edit(commit = true) {
            Key.allKeys.forEach { remove(it) }
        }

        // Updating the night mode preference isn't reflected dynamically.
        withContext(Dispatchers.Main) {
            AppCompatDelegate.setDefaultNightMode(NightModeSetting.SYSTEM.toNightMode())
        }
    }

    // region Debug

    override fun getDebugMocked(): Boolean {
        return sharedPreferences.getBoolean("debug_mock", false)
    }

    override fun getMockSubscription(): MockSubscriptionSetting {
        return sharedPreferences.getString("debug_forceSub", null)
            .let { MockSubscriptionSetting.fromValue(it) }
    }

    override fun setMockSubscription(newValue: MockSubscriptionSetting) {
        sharedPreferences.edit { putString("debug_forceSub", newValue.value) }
        _mockSubscription.tryEmit(newValue)
    }

    // endregion
}
