package dev.esnault.bunpyro.android.screen.grammarpoint.adapter.meaning

import android.app.Activity
import android.content.Context
import android.content.res.ColorStateList
import android.text.Spanned
import androidx.core.view.isVisible
import dev.esnault.bunpyro.R
import dev.esnault.bunpyro.android.utils.*
import dev.esnault.bunpyro.android.screen.grammarpoint.GrammarPointViewModel.ViewState as ViewState
import dev.esnault.bunpyro.android.display.adapter.ViewStatePagerAdapter
import dev.esnault.bunpyro.android.res.longTextResId
import dev.esnault.bunpyro.android.res.srsString
import dev.esnault.bunpyro.common.addFocusRemover
import dev.esnault.bunpyro.common.getColorCompat
import dev.esnault.bunpyro.common.hide
import dev.esnault.bunpyro.common.show
import dev.esnault.bunpyro.databinding.LayoutGrammarPointMeaningBinding
import dev.esnault.bunpyro.domain.DomainConfig
import dev.esnault.bunpyro.domain.entities.grammar.GrammarPoint


class MeaningViewHolder(
    private val activity: Activity,
    private val binding: LayoutGrammarPointMeaningBinding,
    private val listener: Listener
) : ViewStatePagerAdapter.ViewHolder(binding.root) {

    data class Listener(
        val onStudy: () -> Unit,
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
        } else if (newState.furiganaShown != oldState.furiganaShown) {
            updateFuriganaShown(newState.furiganaShown)
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

        bindTags(grammarPoint)
    }

    private fun bindTags(grammarPoint: GrammarPoint) {
        // JLPT
        binding.jlptTag.isVisible = true
        binding.jlptTag.setText(grammarPoint.jlpt.longTextResId)

        // SRS
        binding.srsTag.isVisible = true
        binding.srsTag.text = srsString(context, grammarPoint.srsLevel)

        // SRS color
        val srsColorResId = if (grammarPoint.srsLevel == DomainConfig.STUDY_BURNED) {
            R.color.hanko_gold
        } else {
            R.color.hanko
        }
        val srsColor = context.getColorCompat(srsColorResId)
        binding.srsTag.backgroundTintList = ColorStateList.valueOf(srsColor)
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
}
