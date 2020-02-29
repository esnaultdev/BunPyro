package dev.esnault.bunpyro.android.screen.lessons

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dev.esnault.bunpyro.databinding.ItemLessonGrammarPointBinding
import dev.esnault.bunpyro.domain.entities.GrammarPointOverview


class LessonGrammarAdapter(
    context: Context
) : RecyclerView.Adapter<LessonGrammarAdapter.ViewHolder>() {

    private val inflater = LayoutInflater.from(context)

    var grammarPoints: List<GrammarPointOverview> = mutableListOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemLessonGrammarPointBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(grammarPoints[position])
    }

    override fun getItemCount(): Int = grammarPoints.size

    class ViewHolder(
        private val binding: ItemLessonGrammarPointBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        private val context: Context
            get() = itemView.context

        fun bind(grammarPoint: GrammarPointOverview) {
            binding.japanese.text = grammarPoint.title
            binding.english.text = grammarPoint.meaning
        }
    }
}
