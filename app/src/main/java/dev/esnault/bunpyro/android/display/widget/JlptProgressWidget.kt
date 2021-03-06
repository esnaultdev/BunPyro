package dev.esnault.bunpyro.android.display.widget

import android.animation.ObjectAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.ProgressBar
import android.widget.TextView
import com.google.android.material.progressindicator.LinearProgressIndicator
import dev.esnault.bunpyro.R
import dev.esnault.bunpyro.common.Alpha
import dev.esnault.bunpyro.common.getThemeColor
import dev.esnault.bunpyro.common.withAlpha
import dev.esnault.bunpyro.databinding.WidgetJlptProgressBinding
import dev.esnault.bunpyro.domain.entities.JlptProgress
import kotlin.properties.Delegates


class JlptProgressWidget : FrameLayout {

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    private val binding: WidgetJlptProgressBinding
    private val animators = mutableListOf<ObjectAnimator>()

    init {
        val inflater = LayoutInflater.from(context)
        binding = WidgetJlptProgressBinding.inflate(inflater, this, true)
        initProgress()
    }

    var progress: JlptProgress? by Delegates.observable(null) { _, oldValue, newValue ->
        if (oldValue != newValue) {
            update(oldValue, newValue)
        }
    }

    private fun cancelAnimators() {
        animators.forEach { it.cancel() }
        animators.clear()
    }

    private fun update(oldProgress: JlptProgress?, progress: JlptProgress?) {
        cancelAnimators()

        var delay = 0L
        delay = update(binding.n5Progress, binding.n5Count, oldProgress?.n5, progress?.n5, delay)
        delay = update(binding.n4Progress, binding.n4Count, oldProgress?.n4, progress?.n4, delay)
        delay = update(binding.n3Progress, binding.n3Count, oldProgress?.n3, progress?.n3, delay)
        delay = update(binding.n2Progress, binding.n2Count, oldProgress?.n2, progress?.n2, delay)
        update(binding.n1Progress, binding.n1Count, oldProgress?.n1, progress?.n1, delay)
    }

    private fun update(
        progressBar: ProgressBar,
        countTextView: TextView,
        oldProgress: JlptProgress.Progress?,
        progress: JlptProgress.Progress?,
        delay: Long
    ): Long {
        if (oldProgress == progress) {
            return delay
        }

        // The max of the progress bar is always set to 100 to simplify the animations
        val progressPercent = if (progress != null && progress.total > 0) {
            progress.studied * 100 / progress.total
        } else {
            0
        }
        val animator = ObjectAnimator.ofInt(progressBar, "progress", progressPercent).apply {
            duration = 300L
            startDelay = delay + 50L
            animators.add(this)
            start()
        }

        val countText = if (progress != null) {
            context.getString(R.string.home_progress_count, progress.studied, progress.total)
        } else {
            ""
        }
        countTextView.text = countText

        return animator.startDelay
    }

    private fun initProgress() {
        binding.n5Progress.initProgress()
        binding.n4Progress.initProgress()
        binding.n3Progress.initProgress()
        binding.n2Progress.initProgress()
        binding.n1Progress.initProgress()
    }

    // It would be better to do this in XML, but SDK 21 has some troubles when using theme colors.
    private fun LinearProgressIndicator.initProgress() {
        trackColor = context.getThemeColor(R.attr.colorOnSurface).withAlpha(Alpha.p08)
    }
}
