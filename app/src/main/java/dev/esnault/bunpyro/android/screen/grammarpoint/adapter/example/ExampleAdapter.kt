package dev.esnault.bunpyro.android.screen.grammarpoint.adapter.example

import android.content.Context
import android.text.Spanned
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dev.esnault.bunpyro.android.utils.BunProTextListener
import dev.esnault.bunpyro.android.utils.processBunproString
import dev.esnault.bunpyro.common.hide
import dev.esnault.bunpyro.common.show
import dev.esnault.bunpyro.databinding.ItemExampleSentenceBinding
import dev.esnault.bunpyro.domain.entities.grammar.ExampleSentence


class ExampleAdapter(context: Context) : RecyclerView.Adapter<ExampleAdapter.ViewHolder>() {

    private val inflater = LayoutInflater.from(context)

    var examples: List<ExampleSentence> = emptyList()
        set(value) {
            val oldValue = field
            field = value

            if (oldValue != value) {
                notifyDataSetChanged()
            }
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemExampleSentenceBinding.inflate(inflater, parent, false)
        return ViewHolder(
            binding
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(examples[position])
    }

    override fun getItemCount(): Int = examples.size

    class ViewHolder(
        private val binding: ItemExampleSentenceBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        private val context: Context
            get() = itemView.context

        private var example: ExampleSentence? = null

        fun bind(example: ExampleSentence) {
            this.example = example

            binding.japanese.text = postProcessJapanese(example.japanese)
            binding.english.text = postProcessString(example.english)

            if (!example.nuance.isNullOrBlank()) {
                binding.nuance.text = postProcessString(example.nuance)
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

        private fun postProcessJapanese(source: String): Spanned {
            return context.processBunproString(
                source, bunProTextListener, secondaryBreaks = false, furiganize = true)
        }

        private fun postProcessString(source: String): Spanned {
            return context.processBunproString(
                source, bunProTextListener, secondaryBreaks = false, furiganize = false)
        }

        // endregion
    }
}
