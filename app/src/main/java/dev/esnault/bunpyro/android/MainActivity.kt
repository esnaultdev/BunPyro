package dev.esnault.bunpyro.android

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import dev.esnault.bunpyro.R
import dev.esnault.bunpyro.android.res.toNightMode
import dev.esnault.bunpyro.data.repository.settings.ISettingsRepository
import kotlinx.coroutines.*
import org.koin.android.ext.android.inject

class MainActivity : AppCompatActivity() {

    companion object {
        init {
            // Load the custom SQLite library with its tokenizers
            System.loadLibrary("sqliteX")
            System.loadLibrary("tokenizers")
        }
    }

    private val settingsRepo : ISettingsRepository by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        initNightMode()
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
    }

    private fun initNightMode() {
        // Even though the shared preferences can sometimes load slowly (if the system is under load
        // by other apps), we need to set the theme before anything else to avoid a white flash
        // for dark theme users.
        runBlocking {
            val setting = settingsRepo.getNightMode()
            val nightMode = setting.toNightMode()

            val currentNightMode = AppCompatDelegate.getDefaultNightMode()
            if (currentNightMode != nightMode) {
                AppCompatDelegate.setDefaultNightMode(nightMode)
            }
        }
    }
}
