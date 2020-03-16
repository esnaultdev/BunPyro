package dev.esnault.bunpyro.android.res

import androidx.appcompat.app.AppCompatDelegate
import dev.esnault.bunpyro.domain.entities.settings.NightModeSetting


fun NightModeSetting.toNightMode() = when (this) {
    NightModeSetting.ALWAYS -> AppCompatDelegate.MODE_NIGHT_YES
    NightModeSetting.NEVER -> AppCompatDelegate.MODE_NIGHT_NO
    NightModeSetting.SYSTEM -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
}
