package dev.esnault.bunpyro.android.screen.grammarpoint.adapter.example

import android.content.Context
import android.text.Spanned
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.AutoTransition
import androidx.transition.ChangeBounds
import androidx.transition.TransitionManager
import androidx.transition.TransitionSet
import com.transitionseverywhere.ChangeText
import dev.esnault.bunpyro.R
import dev.esnault.bunpyro.android.screen.ScreenConfig
import dev.esnault.bunpyro.android.screen.grammarpoint.GrammarPointViewModel.ViewState
import dev.esnault.bunpyro.android.utils.*
import dev.esnault.bunpyro.databinding.ItemExampleSentenceBinding


class ExampleAdapter(
    context: Context,
    private val listener: ExamplesViewHolder.Listener
) : RecyclerView.Adapter<ExampleAdapter.ViewHolder>() {

    private val inflater = LayoutInflater.from(context)

    var viewState: ViewState? = null
        set(value) {
            val oldValue = field
            field = value

            if (oldValue?.grammarPoint?.sentences != value?.grammarPoint?.sentences) {
                notifyDataSetChanged()
            } else {
                // Use an empty payload so that the recycler view keeps the current view holders
                notifyItemRangeChanged(0, itemCount, Unit)
            }
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemExampleSentenceBinding.inflate(inflater, parent, false)
        return ViewHolder(binding, listener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val viewState = viewState!!
        val example = viewState.examples[position]
        holder.bind(example, viewState.furiganaShown)
    }

    override fun onViewRecycled(holder: ViewHolder) {
        super.onViewRecycled(holder)
        holder.unbind()
    }

    override fun getItemCount(): Int = viewState?.grammarPoint?.sentences?.size ?: 0

    class ViewHolder(
        private val binding: ItemExampleSentenceBinding,
        private val listener: ExamplesViewHolder.Listener
    ) : RecyclerView.ViewHolder(binding.root) {

        private val context: Context
            get() = itemView.context

        private var example: ViewState.Example? = null
        private var furiganaShown: Boolean = false

        init {
            binding.expandButton.setOnClickListener {
                example?.let(listener.onToggleSentence)
            }
        }

        fun bind(example: ViewState.Example, furiganaShown: Boolean) {
            val sentenceChanged = example.sentence != this.example?.sentence
            val furiganaChanged = furiganaShown != this.furiganaShown
            val expansionChanged = example.collapsed != this.example?.collapsed

            this.example = example
            this.furiganaShown = furiganaShown

            if (sentenceChanged) {
                bindExample(example, furiganaShown)
            } else if (furiganaChanged) {
                updateFurigana(furiganaShown)
            } else if (expansionChanged) {
                updateExpansion(example)
            }
        }

        fun unbind() {
            example = null
            furiganaShown = false
        }

        private fun bindExample(example: ViewState.Example, furiganaShown: Boolean) {
            val sentence = example.sentence
            binding.japanese.text = postProcessJapanese(sentence.japanese, furiganaShown)
            binding.english.text = postProcessString(sentence.english, furiganaShown)

            val nuanceIsBlank = sentence.nuance.isNullOrBlank()
            if (!nuanceIsBlank) {
                binding.nuance.text = postProcessString(sentence.nuance!!, furiganaShown)
            }

            updateExpansion(example.collapsed, nuanceIsBlank)
        }

        private fun updateFurigana(furiganaShown: Boolean) {
            val transition = TransitionSet().apply {
                ordering = TransitionSet.ORDERING_TOGETHER
                duration = ScreenConfig.Transition.exampleChangeDuration
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

        private fun updateExpansion(example: ViewState.Example) {
            val transition = NamedAutoTransition().apply {
                // The change bounds needs to be in sync with the animations made by the recycler
                // view moving this view to its new position
                ordering = TransitionSet.ORDERING_TOGETHER

                fadeOut.duration = 50L
                changeBounds.duration = ScreenConfig.Transition.exampleChangeDuration
                fadeIn.startDelay = ScreenConfig.Transition.exampleChangeDuration - 50L
                fadeIn.duration = 50L

                fadeIn.excludeTarget(binding.frameLayout, true)
                fadeOut.excludeTarget(binding.frameLayout, true)
            }
            TransitionManager.beginDelayedTransition(binding.frameLayout, transition)

            val nuanceIsBlank = example.sentence.nuance.isNullOrBlank()
            updateExpansion(example.collapsed, nuanceIsBlank)
        }

        private fun updateExpansion(collapsed: Boolean, nuanceIsBlank: Boolean) {
            binding.nuance.isVisible = !collapsed && !nuanceIsBlank
            binding.english.isVisible = !collapsed

            val buttonResId = if (collapsed) {
                R.string.grammarPoint_tab_examples_expand
            } else {
                R.string.grammarPoint_tab_examples_collapse
            }
            binding.expandButton.setText(buttonResId)
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
