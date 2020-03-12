package dev.esnault.bunpyro.android.screen.lessons

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import dev.esnault.bunpyro.android.display.adapter.GrammarOverviewAdapter
import dev.esnault.bunpyro.android.display.viewholder.GrammarOverviewViewHolder
import dev.esnault.bunpyro.android.display.widget.ViewStatePagerAdapter
import dev.esnault.bunpyro.databinding.ItemLessonBinding
import dev.esnault.bunpyro.domain.entities.Lesson


class LessonAdapter(
    context: Context,
    private val listener: GrammarOverviewViewHolder.Listener
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
        return ViewHolder(binding, listener)
    }

    override fun onBindPageViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(lessons[position])
    }

    override fun getItemCount(): Int = lessons.size

    class ViewHolder(
        private val binding: ItemLessonBinding,
        listener: GrammarOverviewViewHolder.Listener
    ) : ViewStatePagerAdapter.ViewHolder(binding.root) {

        private val context: Context
            get() = itemView.context

        private val grammarAdapter =
            GrammarOverviewAdapter(
                context,
                listener
            )

        init {
            binding.recyclerView.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = grammarAdapter
            }
        }

        fun bind(lesson: Lesson) {
            grammarAdapter.grammarPoints = lesson.points

            binding.comingSoon.isVisible = lesson.size == 0
        }
    }
}
