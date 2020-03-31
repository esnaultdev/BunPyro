package dev.esnault.bunpyro.android.screen.lessons

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.graphics.drawable.ScaleDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import dev.esnault.bunpyro.R
import dev.esnault.bunpyro.android.display.viewholder.GrammarOverviewViewHolder
import dev.esnault.bunpyro.android.display.adapter.ViewStatePagerAdapter
import dev.esnault.bunpyro.common.Alpha
import dev.esnault.bunpyro.common.getThemeColor
import dev.esnault.bunpyro.common.withAlpha
import dev.esnault.bunpyro.databinding.ItemJlptLessonBinding
import dev.esnault.bunpyro.databinding.TabLessonBinding
import dev.esnault.bunpyro.domain.entities.grammar.GrammarPointOverview
import dev.esnault.bunpyro.domain.entities.JlptLesson


class JlptLessonAdapter(
    context: Context,
    private val onGrammarClicked: (point: GrammarPointOverview) -> Unit
) : ViewStatePagerAdapter<JlptLessonAdapter.ViewHolder>() {

    private val inflater = LayoutInflater.from(context)

    var jlptLessons: List<JlptLesson> = mutableListOf()
        set(value) {
            val oldValue = field
            field = value

            if (oldValue != value) {
                notifyDataSetChanged()
            }
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemJlptLessonBinding.inflate(inflater, parent, false)
        return ViewHolder(binding, onGrammarClicked)
    }

    override fun onBindPageViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(jlptLessons[position])
    }

    override fun getItemCount(): Int = jlptLessons.size

    class ViewHolder(
        private val binding: ItemJlptLessonBinding,
        onGrammarClicked: (point: GrammarPointOverview) -> Unit
    ) : ViewStatePagerAdapter.ViewHolder(binding.root) {

        private val context: Context
            get() = itemView.context

        private val layoutInflater: LayoutInflater
            get() = LayoutInflater.from(context)

        private val lessonAdapter = kotlin.run {
            val grammarPointListener = GrammarOverviewViewHolder.Listener(
                onGrammarClicked = onGrammarClicked
            )
            LessonAdapter(context, grammarPointListener)
        }

        init {
            binding.lessonsPager.adapter = lessonAdapter
            setupTabs()
            bindPagerToTabs()
        }

        private fun bindPagerToTabs() {
            TabLayoutMediator(binding.lessonsTabs, binding.lessonsPager) { tab, position ->
                tab.apply {
                    val tabBinding = TabLessonBinding.inflate(layoutInflater)
                    customView = tabBinding.root
                    tab.tag = tabBinding

                    tabBinding.title.text = (position + 1).toString()
                    updateTabColors(tabBinding, false)

                    lessonAdapter.lessons[position].let { lesson ->
                        val hasGrammar = lesson.size != 0

                        tabBinding.apply {
                            if (!hasGrammar) {
                                progress.visibility = View.INVISIBLE
                            } else {
                                progress.visibility = View.VISIBLE

                                progress.max = lesson.size
                                progress.progress = lesson.studied
                            }
                        }
                    }
                }
            }.attach()
        }

        fun bind(jlptLesson: JlptLesson) {
            lessonAdapter.lessons = jlptLesson.lessons
        }

        // region Tabs

        private fun setupTabs() {
            binding.lessonsTabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabReselected(tab: TabLayout.Tab?) {}

                override fun onTabUnselected(tab: TabLayout.Tab?) {
                    (tab?.tag as? TabLessonBinding)?.let { updateTabColors(it, false) }
                }

                override fun onTabSelected(tab: TabLayout.Tab?) {
                    (tab?.tag as? TabLessonBinding)?.let { updateTabColors(it, true) }
                }
            })
        }

        // Update the progress bar colors for the selected tab.
        // Using a colorStateList for the progress and background drawables doesn't work on API 21
        // so we need to update the drawables colors manually
        private fun updateTabColors(tabBinding: TabLessonBinding, selected: Boolean) {
            (tabBinding.progress.progressDrawable as? LayerDrawable)?.apply {
                findDrawableByLayerId(android.R.id.progress)
                    ?.let { it as? ScaleDrawable }
                    ?.drawable
                    ?.mutate()
                    ?.let { it as? GradientDrawable }
                    ?.apply {
                        val color = if (selected) {
                            tabProgressColorSelected
                        } else {
                            tabProgressColorNormal
                        }
                        setColor(color)
                    }

                findDrawableByLayerId(android.R.id.background)
                    ?.mutate()
                    ?.let { it as? GradientDrawable }
                    ?.apply {
                        val color = if (selected) {
                            tabBackgroundColorSelected
                        } else {
                            tabBackgroundColorNormal
                        }
                        setColor(color)
                    }
            }
        }

        // endregion

        // region Resources

        private val tabProgressColorNormal: Int by lazy(LazyThreadSafetyMode.NONE) {
            context.getThemeColor(R.attr.colorOnSurface).withAlpha(Alpha.p25)
        }
        private val tabProgressColorSelected: Int by lazy(LazyThreadSafetyMode.NONE) {
            context.getThemeColor(R.attr.colorControlActivated)
        }
        private val tabBackgroundColorNormal: Int by lazy(LazyThreadSafetyMode.NONE) {
            context.getThemeColor(R.attr.colorOnSurface).withAlpha(Alpha.p10)
        }
        private val tabBackgroundColorSelected: Int by lazy(LazyThreadSafetyMode.NONE) {
            context.getThemeColor(R.attr.colorControlActivated).withAlpha(Alpha.p20)
        }

        // endregion
    }
}
