package dev.esnault.bunpyro.android.screen.grammarpoint.adapter.meaning

import android.content.Context
import android.text.Spanned
import android.widget.TextView
import androidx.transition.TransitionManager
import dev.esnault.bunpyro.android.screen.grammarpoint.GrammarPointViewModel.ViewState as ViewState
import dev.esnault.bunpyro.android.utils.BunProTextListener
import dev.esnault.bunpyro.android.utils.RubySpan
import dev.esnault.bunpyro.android.utils.processBunproString
import dev.esnault.bunpyro.android.widget.ViewStatePagerAdapter
import dev.esnault.bunpyro.common.hide
import dev.esnault.bunpyro.common.show
import dev.esnault.bunpyro.databinding.LayoutGrammarPointMeaningBinding
import me.saket.bettermovementmethod.BetterLinkMovementMethod


class MeaningViewHolder(
    private val binding: LayoutGrammarPointMeaningBinding,
    private val listener: Listener
) : ViewStatePagerAdapter.ViewHolder(binding.root) {

    data class Listener(
        val onStudy: () -> Unit,
        val onGrammarPointClick: (id: Int) -> Unit
    )

    private val context: Context
        get() = itemView.context

    var viewState: ViewState? = null
        set(value) {
            val oldValue = field
            field = value

            if (oldValue != value) {
                bind(oldValue, value)
            }
        }

    init {
        binding.structureText.movementMethod = BetterLinkMovementMethod.newInstance()
        binding.cautionText.movementMethod = BetterLinkMovementMethod.newInstance()
        binding.nuanceText.movementMethod = BetterLinkMovementMethod.newInstance()
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
        } else if (newState.furiganaShown != oldState?.furiganaShown) {
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
    }

    private fun updateFuriganaShown(furiganaShow: Boolean) {
        TransitionManager.beginDelayedTransition(binding.constraintLayout)

        updateFuriganas(binding.meaning, furiganaShow)
        updateFuriganas(binding.structureText, furiganaShow)
        updateFuriganas(binding.cautionText, furiganaShow)
        updateFuriganas(binding.nuanceText, furiganaShow)
    }

    private fun updateFuriganas(textView: TextView, furiganaShow: Boolean) {
        val spanned = textView.text as? Spanned ?: return
        val rubySpans = spanned.getSpans(0, spanned.length, RubySpan::class.java)

        rubySpans.forEach { rubySpan ->
            rubySpan.visibility = if (furiganaShow) {
                RubySpan.Visibility.VISIBLE
            } else
                RubySpan.Visibility.GONE
            }

        if (rubySpans.isNotEmpty()) {
            textView.requestLayout()
        }
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
