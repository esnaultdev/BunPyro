package dev.esnault.bunpyro.android.screen.grammarpoint.adapter.example

import android.content.Context
import android.content.res.ColorStateList
import android.text.Spanned
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.core.view.isVisible
import androidx.core.view.setPadding
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.ChangeBounds
import androidx.transition.TransitionManager
import androidx.transition.TransitionSet
import dev.esnault.bunpyro.R
import dev.esnault.bunpyro.android.media.SimpleAudioState
import dev.esnault.bunpyro.android.screen.ScreenConfig
import dev.esnault.bunpyro.android.screen.grammarpoint.GrammarPointViewModel.ViewState
import dev.esnault.bunpyro.android.utils.*
import dev.esnault.bunpyro.android.utils.transition.ChangeText
import dev.esnault.bunpyro.android.utils.transition.NamedAutoTransition
import dev.esnault.bunpyro.common.Alpha
import dev.esnault.bunpyro.common.dpToPx
import dev.esnault.bunpyro.common.getThemeColor
import dev.esnault.bunpyro.common.withAlpha
import dev.esnault.bunpyro.databinding.ItemExampleSentenceBinding
import dev.esnault.bunpyro.databinding.ItemExampleSubscriptionBinding
import dev.esnault.bunpyro.domain.entities.media.AudioItem


class ExampleAdapter(
    context: Context,
    private val listener: ExamplesViewHolder.Listener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private enum class ViewType(val value: Int) {
        SENTENCE(0), SUBSCRIBE(1);

        companion object {
            fun fromValue(value: Int): ViewType? {
                return when (value) {
                    SENTENCE.value -> SENTENCE
                    SUBSCRIBE.value -> SUBSCRIBE
                    else -> null
                }
            }
        }
    }

    private val inflater = LayoutInflater.from(context)

    private var hasSubscriptionCta: Boolean = false
    private var _itemCount: Int = 0

    var viewState: ViewState? = null
        set(value) {
            val oldCount = _itemCount
            field = value
            updateItemCount()
            val newCount = _itemCount

            if (oldCount != newCount) {
                notifyDataSetChanged()
            } else {
                // Change of furigana, etc.
                // Use an empty payload so that the recycler view keeps the current view holders
                notifyItemRangeChanged(0, itemCount, Unit)
            }
        }

    override fun getItemViewType(position: Int): Int {
        return if (hasSubscriptionCta && position == itemCount - 1) {
            ViewType.SUBSCRIBE.value
        } else {
            ViewType.SENTENCE.value
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (ViewType.fromValue(viewType)) {
            ViewType.SENTENCE -> {
                val binding = ItemExampleSentenceBinding.inflate(inflater, parent, false)
                SentenceViewHolder(binding, listener)
            }
            ViewType.SUBSCRIBE -> {
                val binding = ItemExampleSubscriptionBinding.inflate(inflater, parent, false)
                SubscribeViewHolder(binding, listener)
            }
            else -> throw IllegalArgumentException("Unknown viewType: $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is SentenceViewHolder -> bindSentenceViewHolder(holder, position)
            is SubscribeViewHolder -> holder.bind(viewState!!.examples.size)
            else -> throw IllegalArgumentException("Unknown ViewHolder: $holder")
        }
    }

    private fun bindSentenceViewHolder(holder: SentenceViewHolder, position: Int) {
        val viewState = viewState!!
        val example = viewState.examples[position]
        val currentAudio = viewState.currentAudio
        val audioState: SimpleAudioState? = when {
            example.sentence.audioLink.isNullOrBlank() -> null
            currentAudio == null -> SimpleAudioState.STOPPED
            currentAudio.item is AudioItem.Example && currentAudio.item.exampleId == example.id -> {
                currentAudio.state.toSimpleState()
            }
            else -> SimpleAudioState.STOPPED
        }
        holder.bind(example, audioState, viewState.furiganaShown)
    }

    override fun onViewRecycled(holder: RecyclerView.ViewHolder) {
        super.onViewRecycled(holder)
        if (holder is SentenceViewHolder) {
            holder.unbind()
        }
    }

    override fun getItemCount(): Int = _itemCount

    private fun updateItemCount() {
        val viewState = viewState
        if (viewState == null) {
            _itemCount = 0
            hasSubscriptionCta = false
        } else {
            val sentencesCount = viewState.grammarPoint.sentences.size
            if (sentencesCount > 1 && !viewState.subStatus.isSubscribed) {
                _itemCount = 2 // 1st example and CTA for the subscription
                hasSubscriptionCta = true
            } else {
                _itemCount = sentencesCount
                hasSubscriptionCta = false
            }
        }
    }

    class SentenceViewHolder(
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

            binding.more.setOnClickListener {
                openMoreMenu()
            }

            binding.audioIcon.setOnClickListener {
                example?.let(listener.onAudioClick)
            }

            // Defining this color in xml has some issues on SDK 21, so we set it programmatically
            val cardLineColor = context.getThemeColor(R.attr.colorOnSurface)
                .withAlpha(Alpha.p20)
            binding.cardView.setStrokeColor(ColorStateList.valueOf(cardLineColor))
            binding.actionsDivider.setBackgroundColor(cardLineColor)
        }

        fun bind(
            example: ViewState.Example,
            audioState: SimpleAudioState?,
            furiganaShown: Boolean
        ) {
            val sentenceChanged = example.sentence != this.example?.sentence
            val furiganaChanged = furiganaShown != this.furiganaShown
            val expansionChanged = example.collapsed != this.example?.collapsed

            this.example = example
            this.furiganaShown = furiganaShown

            when {
                sentenceChanged -> bindExample(example, furiganaShown)
                furiganaChanged -> updateFurigana(furiganaShown)
                expansionChanged -> updateExpansion(example)
            }
            bindAudioState(audioState)
        }

        fun unbind() {
            example = null
            furiganaShown = false
        }

        private fun bindExample(example: ViewState.Example, furiganaShown: Boolean) {
            val sentence = example.sentence
            binding.japanese.text =
                postProcessJapanese(sentence.japanese, furiganaShown, example.titles)
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
            val transition = NamedAutoTransition()
                .apply {
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

        private fun bindAudioState(audioState: SimpleAudioState?) {
            if (audioState == null) {
                binding.audioIcon.isVisible = false
                binding.audioLoading.isVisible = false
            } else {
                binding.audioIcon.isVisible = true
                val (iconRes, loadingVisible) = when (audioState) {
                    SimpleAudioState.STOPPED -> R.drawable.ic_play_arrow_24dp to false
                    SimpleAudioState.LOADING -> R.drawable.ic_stop_24dp to true
                    SimpleAudioState.PLAYING -> R.drawable.ic_stop_24dp to false
                }
                binding.audioIcon.setImageResource(iconRes)
                binding.audioLoading.isVisible = loadingVisible

                val iconPadding = if (loadingVisible) 16f else 12f
                binding.audioIcon.setPadding(iconPadding.dpToPx(context.resources.displayMetrics))
            }
        }

        // region Text processing

        private val bunProTextListener = BunProTextListener(
            onGrammarPointClick = listener.onGrammarPointClick
        )

        private fun postProcessJapanese(
            source: String,
            furigana: Boolean,
            titles: List<String>
        ): Spanned {
            return context.processBunproString(
                source = source,
                listener = bunProTextListener,
                secondaryBreaks = false,
                showFurigana = furigana,
                furiganize = true
            ).postEmphasis(context, titles)
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

        // region More menu

        private fun openMoreMenu() {
            PopupMenu(context, binding.more).apply {
                menuInflater.inflate(R.menu.example, menu)
                show()
                setOnMenuItemClickListener { item -> onMenuMoreItemClick(item) }
            }
        }

        private fun onMenuMoreItemClick(item: MenuItem): Boolean {
            return when (item.itemId) {
                R.id.copy_japanese -> {
                    example?.let { listener.onCopyJapanese(it) }
                    true
                }
                R.id.copy_english -> {
                    example?.let { listener.onCopyEnglish(it) }
                    true
                }
                else -> false
            }
        }

        // endregion
    }

    class SubscribeViewHolder(
        private val binding: ItemExampleSubscriptionBinding,
        private val listener: ExamplesViewHolder.Listener
    ) : RecyclerView.ViewHolder(binding.root) {

        private val context: Context
            get() = itemView.context

        init {
            // Defining this color in xml has some issues on SDK 21, so we set it programmatically
            val cardLineColor = context.getThemeColor(R.attr.colorOnSurface).withAlpha(Alpha.p20)
            binding.cardView.setStrokeColor(ColorStateList.valueOf(cardLineColor))

            binding.button.setOnClickListener { listener.onSubscribeClick() }
        }

        fun bind(sentencesCount: Int) {
            binding.explanation.text =
                context.getString(R.string.subscription_examples_message, sentencesCount - 1)
        }
    }
}
