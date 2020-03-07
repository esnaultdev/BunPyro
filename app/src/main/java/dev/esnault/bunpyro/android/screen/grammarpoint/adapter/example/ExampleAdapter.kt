package dev.esnault.bunpyro.android.screen.grammarpoint.adapter.example

import android.content.Context
import android.text.Spanned
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dev.esnault.bunpyro.android.screen.grammarpoint.GrammarPointViewModel.ViewState as ViewState
import dev.esnault.bunpyro.android.utils.BunProTextListener
import dev.esnault.bunpyro.android.utils.processBunproString
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
                notifyItemRangeChanged(0, itemCount)
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

    override fun getItemCount(): Int = viewState?.grammarPoint?.sentences?.size ?: 0

    class ViewHolder(
        private val binding: ItemExampleSentenceBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        private val context: Context
            get() = itemView.context

        private var example: ExampleSentence? = null

        fun bind(example: ExampleSentence, furiganaShown: Boolean) {
            this.example = example

            binding.japanese.text = postProcessJapanese(example.japanese, furiganaShown)
            binding.english.text = postProcessString(example.english, furiganaShown)

            if (!example.nuance.isNullOrBlank()) {
                binding.nuance.text = postProcessString(example.nuance, furiganaShown)
                binding.nuance.show()
            } else {
                binding.nuance.hide()
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
