package dev.esnault.bunpyro.android.screen.lessons

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dev.esnault.bunpyro.common.setVisible
import dev.esnault.bunpyro.databinding.ItemLessonGrammarPointBinding
import dev.esnault.bunpyro.domain.entities.grammar.GrammarPointOverview


class LessonGrammarAdapter(
    context: Context,
    private val onGrammarClicked: (point: GrammarPointOverview) -> Unit
) : RecyclerView.Adapter<LessonGrammarAdapter.ViewHolder>() {

    private val inflater = LayoutInflater.from(context)

    var grammarPoints: List<GrammarPointOverview> = mutableListOf()
        set(value) {
            val oldValue = field
            field = value

            if (oldValue != value) {
                notifyDataSetChanged()
            }
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemLessonGrammarPointBinding.inflate(inflater, parent, false)
        return ViewHolder(binding, onGrammarClicked)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(grammarPoints[position], position == grammarPoints.lastIndex)
    }

    override fun getItemCount(): Int = grammarPoints.size

    class ViewHolder(
        private val binding: ItemLessonGrammarPointBinding,
        private val onGrammarClicked: (point: GrammarPointOverview) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        private val context: Context
            get() = itemView.context

        private var grammarPoint: GrammarPointOverview? = null

        init {
            binding.root.setOnClickListener {
                grammarPoint?.let(onGrammarClicked)
            }
        }

        fun bind(grammarPoint: GrammarPointOverview, isLast: Boolean) {
            this.grammarPoint = grammarPoint

            binding.japanese.text = grammarPoint.title
            binding.english.text = grammarPoint.meaning

            binding.bottomDivider.setVisible(!isLast)

            // Completion
            binding.background.isEnabled = !grammarPoint.incomplete
            binding.japanese.isEnabled = !grammarPoint.incomplete
        }
    }
}
