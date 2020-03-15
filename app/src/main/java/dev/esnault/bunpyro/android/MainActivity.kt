package dev.esnault.bunpyro.android

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO
import dev.esnault.bunpyro.R

class MainActivity : AppCompatActivity() {

    companion object {
        init {
            // Load the custom SQLite library with its tokenizers
            System.loadLibrary("sqliteX")
            System.loadLibrary("tokenizers")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        disableNightMode()

        setContentView(R.layout.activity_main)
    }

    private fun disableNightMode() {
        // Temporarily disable the night theme until we have time to work on it
        val nightMode = AppCompatDelegate.getDefaultNightMode()
        if (nightMode != MODE_NIGHT_NO) {
            AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_NO)
        }
    }
}
