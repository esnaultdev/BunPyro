package dev.esnault.bunpyro.android.screen.grammarpoint.adapter.meaning

import android.app.Activity
import android.content.Context
import android.graphics.drawable.Drawable
import android.text.Spanned
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import dev.esnault.bunpyro.R
import dev.esnault.bunpyro.android.utils.*
import dev.esnault.bunpyro.android.screen.grammarpoint.GrammarPointViewModel.ViewState as ViewState
import dev.esnault.bunpyro.android.screen.grammarpoint.GrammarPointViewModel.ViewState.ReviewAction
import dev.esnault.bunpyro.android.display.adapter.ViewStatePagerAdapter
import dev.esnault.bunpyro.android.res.longTextResId
import dev.esnault.bunpyro.android.res.srsString
import dev.esnault.bunpyro.common.*
import dev.esnault.bunpyro.databinding.LayoutGrammarPointMeaningBinding
import dev.esnault.bunpyro.domain.DomainConfig
import dev.esnault.bunpyro.domain.entities.grammar.GrammarPoint


class MeaningViewHolder(
    private val activity: Activity,
    private val binding: LayoutGrammarPointMeaningBinding,
    private val listener: Listener
) : ViewStatePagerAdapter.ViewHolder(binding.root) {

    data class Listener(
        val onAddToReviews: () -> Unit,
        val onGrammarPointClick: (id: Long) -> Unit
    )

    private val context: Context
        get() = activity

    var viewState: ViewState? = null
        set(value) {
            val oldValue = field
            field = value

            if (oldValue != value) {
                bind(oldValue, value)
            }
        }

    init {
        binding.structureText.movementMethod = BestLinkMovementMethod()
        binding.cautionText.movementMethod = BestLinkMovementMethod()
        binding.nuanceText.movementMethod = BestLinkMovementMethod()

        binding.meaningScrollView.addFocusRemover(activity)

        binding.reviewAdd.setOnClickListener { listener.onAddToReviews() }
    }

    private fun bind(oldState: ViewState?, newState: ViewState?) {
        if (newState == null) {
            binding.constraintLayout.hide()
            return
        }

        if (oldState == null) {
            binding.constraintLayout.show()
        }

        if (newState.grammarPoint != oldState?.grammarPoint) {
            bindFields(newState)
            bindTags(newState.grammarPoint)
            bindReviews(newState)
        } else if (newState.furiganaShown != oldState.furiganaShown) {
            updateFuriganaShown(newState.furiganaShown)
        } else if (newState.reviewAction != oldState.reviewAction) {
            bindReviews(newState)
        }
    }

    private fun bindFields(viewState: ViewState) {
        val grammarPoint = viewState.grammarPoint
        val showFurigana = viewState.furiganaShown
        binding.meaning.text = postProcessString(grammarPoint.meaning, furigana = showFurigana)

        val structure = grammarPoint.structure
        if (!structure.isNullOrBlank()) {
            binding.structureGroup.show()
            binding.structureText.text = postProcessString(structure, furigana = showFurigana)
        } else {
            binding.structureGroup.hide()
        }

        val caution = grammarPoint.caution
        if (!caution.isNullOrBlank()) {
            binding.cautionGroup.show()
            binding.cautionText.text = postProcessString(caution, furigana = showFurigana)
        } else {
            binding.cautionGroup.hide()
        }

        val nuance = grammarPoint.nuance
        if (!nuance.isNullOrBlank()) {
            binding.nuanceGroup.show()
            binding.nuanceText.text =
                postProcessString(nuance, furigana = showFurigana, secondaryBreaks = false)
        } else {
            binding.nuanceGroup.hide()
        }

        binding.jlptTag.isVisible = true
        binding.jlptTag.setText(grammarPoint.jlpt.longTextResId)
    }

    private fun bindTags(grammarPoint: GrammarPoint) {
        binding.jlptTag.isVisible = true
        binding.jlptTag.setText(grammarPoint.jlpt.longTextResId)

        binding.srsTag.isVisible = true
        binding.srsTag.text = srsString(context, grammarPoint.srsLevel)
    }

    private fun bindReviews(viewState: ViewState) {
        val grammarPoint = viewState.grammarPoint
        val srsLevel = grammarPoint.srsLevel
        val studied = srsLevel != null

        binding.reviewTitle.isVisible = true

        binding.reviewAdd.isVisible = !studied
        binding.reviewProgress.isVisible = studied
        binding.reviewProgressText.isVisible = studied

        if (srsLevel != null) {
            val burned = srsLevel == DomainConfig.STUDY_BURNED
            binding.reviewProgress.progressDrawable = buildProgressDrawable(burned)
            binding.reviewProgress.progress = srsLevel
            binding.reviewProgressText.text = srsString(context, srsLevel)
        }

        binding.reviewAdd.progress = viewState.reviewAction == ReviewAction.ADD
    }

    private fun updateFuriganaShown(furiganaShow: Boolean) {
        val visibility = furiganaShow.toRubyVisibility()
        updateTextViewFuriganas(binding.meaning, visibility)
        updateTextViewFuriganas(binding.structureText, visibility)
        updateTextViewFuriganas(binding.cautionText, visibility)
        updateTextViewFuriganas(binding.nuanceText, visibility)
    }

    private val bunProTextListener = BunProTextListener(
        onGrammarPointClick = listener.onGrammarPointClick
    )

    private fun postProcessString(
        source: String,
        furigana: Boolean,
        secondaryBreaks: Boolean = true
    ): Spanned {
        return context.processBunproString(
            source = source,
            listener = bunProTextListener,
            secondaryBreaks = secondaryBreaks,
            showFurigana = furigana,
            furiganize = false
        )
    }

    // Drawable used for the progress bars
    // I would prefer using XML to define it, but SDK 21 has some troubles when using theme colors.
    private fun buildProgressDrawable(burned: Boolean): Drawable {
        val backgroundColor = context.getThemeColor(R.attr.colorOnSurface).withAlpha(Alpha.p10)
        val progressColorResId = if (burned) R.color.hanko_gold else R.color.hanko
        val progressColor = context.getColorCompat(progressColorResId)
        val cornerRadius = context.resources.getDimension(R.dimen.srs_progressbar_height)

        return buildHorizontalProgressDrawable(backgroundColor, progressColor, cornerRadius)
    }
}
