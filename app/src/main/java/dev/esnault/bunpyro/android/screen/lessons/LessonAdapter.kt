package dev.esnault.bunpyro.android.screen.lessons

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dev.esnault.bunpyro.common.setVisible
import dev.esnault.bunpyro.databinding.ItemLessonBinding
import dev.esnault.bunpyro.domain.entities.Lesson


class LessonAdapter(
    context: Context,
    private val onGrammarClicked: (id: Int) -> Unit
) : RecyclerView.Adapter<LessonAdapter.ViewHolder>() {

    private val inflater = LayoutInflater.from(context)

    var lessons: List<Lesson> = mutableListOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemLessonBinding.inflate(inflater, parent, false)
        return ViewHolder(binding, onGrammarClicked)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(lessons[position])
    }

    override fun getItemCount(): Int = lessons.size

    class ViewHolder(
        private val binding: ItemLessonBinding,
        onGrammarClicked: (id: Int) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

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
