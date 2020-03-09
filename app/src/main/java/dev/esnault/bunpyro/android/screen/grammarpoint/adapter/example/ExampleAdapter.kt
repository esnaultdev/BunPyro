package dev.esnault.bunpyro.android.screen.grammarpoint.adapter.example

import android.content.Context
import android.text.Spanned
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.TextSwitcher
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.ChangeBounds
import androidx.transition.TransitionManager
import androidx.transition.TransitionSet
import com.transitionseverywhere.ChangeText
import dev.esnault.bunpyro.R
import dev.esnault.bunpyro.android.screen.ScreenConfig
import dev.esnault.bunpyro.android.screen.grammarpoint.GrammarPointViewModel.ViewState
import dev.esnault.bunpyro.android.utils.*
import dev.esnault.bunpyro.common.hide
import dev.esnault.bunpyro.common.show
import dev.esnault.bunpyro.databinding.ItemExampleSentenceBinding
import dev.esnault.bunpyro.domain.entities.grammar.ExampleSentence


class ExampleAdapter(context: Context) : RecyclerView.Adapter<ExampleAdapter.ViewHolder>() {

    private val inflater = LayoutInflater.from(context)

    var viewState: ViewState? = null
        set(value) {
            val oldValue = field
            field = value

            if (oldValue?.grammarPoint?.sentences != value?.grammarPoint?.sentences) {
                notifyDataSetChanged()
            } else if (oldValue?.furiganaShown != value?.furiganaShown) {
                // Use an empty payload so that the recycler view keeps the current view holders
                notifyItemRangeChanged(0, itemCount, Unit)
            }
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemExampleSentenceBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val viewState = viewState!!
        val example = viewState.grammarPoint.sentences[position]
        holder.bind(example, viewState.furiganaShown)
    }

    override fun onViewRecycled(holder: ViewHolder) {
        super.onViewRecycled(holder)
        holder.unbind()
    }

    override fun getItemCount(): Int = viewState?.grammarPoint?.sentences?.size ?: 0

    class ViewHolder(
        private val binding: ItemExampleSentenceBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        private val context: Context
            get() = itemView.context

        private var example: ExampleSentence? = null
        private var furiganaShown: Boolean = false

        fun bind(example: ExampleSentence, furiganaShown: Boolean) {
            val exampleChanged = example != this.example
            val furiganaChanged = furiganaShown != this.furiganaShown

            this.example = example
            this.furiganaShown = furiganaShown

            if (exampleChanged) {
                bindExample(example, furiganaShown)
            } else if (furiganaChanged) {
                updateFurigana(furiganaShown)
            }
        }

        fun unbind() {
            example = null
            furiganaShown = false
        }

        private fun bindExample(example: ExampleSentence, furiganaShown: Boolean) {
            binding.japanese.text = postProcessJapanese(example.japanese, furiganaShown)
            binding.english.text = postProcessString(example.english, furiganaShown)

            if (!example.nuance.isNullOrBlank()) {
                binding.nuance.text = postProcessString(example.nuance, furiganaShown)
                binding.nuance.show()
            } else {
                binding.nuance.hide()
            }
        }

        private fun updateFurigana(furiganaShown: Boolean) {
            val transition = TransitionSet().apply {
                ordering = TransitionSet.ORDERING_TOGETHER
                duration = ScreenConfig.Transition.furiganaDuration
                addTransition(ChangeText().setChangeBehavior(ChangeText.CHANGE_BEHAVIOR_OUT_IN))
                addTransition(ChangeBounds())
            }
            TransitionManager.beginDelayedTransition(binding.frameLayout, transition)

            val visibility = furiganaShown.toRubyVisibility()
            updateTextViewFuriganas(binding.japanese, visibility)
            updateTextViewFuriganas(binding.english, visibility)
            if (binding.nuance.isVisible) {
                updateTextViewFuriganas(binding.nuance, visibility)
            }
        }

        // region Text processing

        private val bunProTextListener = BunProTextListener(
            // TODO properly bind this listener
            onGrammarPointClick = {}
        )

        private fun postProcessJapanese(source: String, furigana: Boolean): Spanned {
            return context.processBunproString(
                source = source,
                listener = bunProTextListener,
                secondaryBreaks = false,
                showFurigana = furigana,
                furiganize = true
            )
        }

        private fun postProcessString(source: String, furigana: Boolean): Spanned {
            return context.processBunproString(
                source = source,
                listener = bunProTextListener,
                secondaryBreaks = false,
                showFurigana = furigana,
                furiganize = false
            )
        }

        // endregion
    }
}
