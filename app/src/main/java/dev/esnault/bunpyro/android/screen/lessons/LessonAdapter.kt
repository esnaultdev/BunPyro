package dev.esnault.bunpyro.android.screen.lessons

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import dev.esnault.bunpyro.android.widget.ViewStatePagerAdapter
import dev.esnault.bunpyro.common.setVisible
import dev.esnault.bunpyro.databinding.ItemLessonBinding
import dev.esnault.bunpyro.domain.entities.GrammarPointOverview
import dev.esnault.bunpyro.domain.entities.Lesson


class LessonAdapter(
    context: Context,
    private val onGrammarClicked: (point: GrammarPointOverview) -> Unit
) : ViewStatePagerAdapter<LessonAdapter.ViewHolder>() {

    private val inflater = LayoutInflater.from(context)

    var lessons: List<Lesson> = mutableListOf()
        set(value) {
            val oldValue = field
            field = value

            if (oldValue != value) {
                notifyDataSetChanged()
            }
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemLessonBinding.inflate(inflater, parent, false)
        return ViewHolder(binding, onGrammarClicked)
    }

    override fun onBindPageViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(lessons[position])
    }

    override fun getItemCount(): Int = lessons.size

    class ViewHolder(
        private val binding: ItemLessonBinding,
        onGrammarClicked: (point: GrammarPointOverview) -> Unit
    ) : ViewStatePagerAdapter.ViewHolder(binding.root) {

        private val context: Context
            get() = itemView.context

        private val grammarAdapter = LessonGrammarAdapter(context, onGrammarClicked)

        init {
            binding.recyclerView.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = grammarAdapter
            }
        }

        fun bind(lesson: Lesson) {
            grammarAdapter.grammarPoints = lesson.points

            binding.comingSoon.setVisible(lesson.size == 0)
        }
    }
}
