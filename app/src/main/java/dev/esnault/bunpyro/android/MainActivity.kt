package dev.esnault.bunpyro.android

import android.animation.*
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import dev.esnault.bunpyro.R
import dev.esnault.bunpyro.android.res.toNightMode
import dev.esnault.bunpyro.common.getThemeColor
import dev.esnault.bunpyro.data.repository.settings.ISettingsRepository
import kotlinx.coroutines.*
import org.koin.android.ext.android.inject


class MainActivity : AppCompatActivity() {

    private val settingsRepo : ISettingsRepository by inject()

    private lateinit var rootContainer: View
    private var splashBackgroundColor: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        overridePendingTransition(0, 0)

        // Get the color of the splash screen before changing theme
        splashBackgroundColor = getThemeColor(R.attr.launchScreenBackgroundColor)

        setTheme(R.style.AppTheme)
        initNightMode()
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        rootContainer = findViewById(android.R.id.content)
        rootContainer.setBackgroundColor(splashBackgroundColor)

        animateContent()
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

    private fun animateContent() {
        AnimatorSet().apply {
            playTogether(backgroundAnimator(), fragmentAnimator())
            start()
        }
    }

    private fun backgroundAnimator(): Animator {
        val startColor = splashBackgroundColor
        val endColor = theme.getThemeColor(R.attr.colorSurface)
        return ObjectAnimator.ofArgb(rootContainer, "backgroundColor", startColor, endColor)
            .apply {
                duration = 600L
            }
    }

    private fun fragmentAnimator(): Animator {
        val fragmentView = findViewById<View>(R.id.nav_host_fragment)
        fragmentView.alpha = 0f
        return ObjectAnimator.ofFloat(fragmentView, "alpha", 1f).apply {
            startDelay = 200L
            duration = 300L
        }
    }
}
