package dev.esnault.bunpyro.android.screen.lessons

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.tabs.TabLayoutMediator
import dev.esnault.bunpyro.databinding.ItemJlptLessonBinding
import dev.esnault.bunpyro.databinding.TabLessonBinding
import dev.esnault.bunpyro.domain.entities.JlptLesson


class JlptLessonAdapter(
    context: Context,
    private val onGrammarClicked: (id: Int) -> Unit
) : RecyclerView.Adapter<JlptLessonAdapter.ViewHolder>() {

    private val inflater = LayoutInflater.from(context)

    var jlptLessons: List<JlptLesson> = mutableListOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemJlptLessonBinding.inflate(inflater, parent, false)
        return ViewHolder(binding, onGrammarClicked)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(jlptLessons[position])
    }

    override fun getItemCount(): Int = jlptLessons.size

    class ViewHolder(
        private val binding: ItemJlptLessonBinding,
        onGrammarClicked: (id: Int) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        private val context: Context
            get() = itemView.context

        private val layoutInflater: LayoutInflater
            get() = LayoutInflater.from(context)

        private val lessonAdapter = LessonAdapter(context, onGrammarClicked)

        init {
            binding.pager.adapter = lessonAdapter
            bindPagerToTabs()
        }

        private fun bindPagerToTabs() {
            TabLayoutMediator(binding.tabs, binding.pager) { tab, position ->
                tab.apply {
                    val tabBinding = TabLessonBinding.inflate(layoutInflater)
                    customView = tabBinding.root

                    text = (position + 1).toString()

                    lessonAdapter.lessons[position].let { lesson ->
                        val hasGrammar = lesson.size != 0

                        tabBinding.progress.apply {
                            if (hasGrammar) {
                                max = lesson.size
                                progress = lesson.studied

                                visibility = View.VISIBLE
                            } else {
                                visibility = View.INVISIBLE
                            }
                        }
                    }
                }
            }.attach()
        }

        fun bind(jlptLesson: JlptLesson) {
            lessonAdapter.lessons = jlptLesson.lessons
        }
    }
}
