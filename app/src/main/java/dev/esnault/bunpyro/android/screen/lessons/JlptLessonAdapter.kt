package dev.esnault.bunpyro.android.screen.lessons

import android.content.Context
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
import dev.esnault.bunpyro.domain.entities.settings.HankoDisplaySetting
import dev.esnault.bunpyro.domain.utils.lazyNone


class JlptLessonAdapter(
    context: Context,
    private val onGrammarClicked: (point: GrammarPointOverview) -> Unit
) : ViewStatePagerAdapter<JlptLessonAdapter.ViewHolder>() {

    private val inflater = LayoutInflater.from(context)

    var viewModel: ViewModel = ViewModel(emptyList(), HankoDisplaySetting.DEFAULT)
        set(value) {
            val oldValue = field
            field = value

            if (oldValue != value) {
                notifyDataSetChanged()
            }
        }

    val jlptLessons: List<JlptLesson>
        get() = viewModel.jlptLessons

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemJlptLessonBinding.inflate(inflater, parent, false)
        return ViewHolder(binding, onGrammarClicked)
    }

    override fun onBindPageViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(jlptLessons[position], viewModel.hankoDisplay)
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

        fun bind(jlptLesson: JlptLesson, hankoDisplay: HankoDisplaySetting) {
            lessonAdapter.viewModel = LessonAdapter.ViewModel(jlptLesson.lessons, hankoDisplay)
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
        // Using a colorStateList for the progress and background drawables isn't available for
        // progress indicators, so we need to update the drawables colors manually.
        private fun updateTabColors(tabBinding: TabLessonBinding, selected: Boolean) {
            tabBinding.progress.apply {
                val indicatorColor = if (selected) {
                    tabProgressIndicatorColorSelected
                } else {
                    tabProgressIndicatorColorNormal
                }
                setIndicatorColor(indicatorColor)

                val trackColor = if (selected) {
                    tabProgressTrackColorSelected
                } else {
                    tabProgressTrackColorNormal
                }
                setTrackColor(trackColor)
            }
        }

        // endregion

        // region Resources

        private val tabProgressIndicatorColorNormal: Int by lazyNone {
            context.getThemeColor(R.attr.colorOnSurface).withAlpha(Alpha.p25)
        }
        private val tabProgressIndicatorColorSelected: Int by lazyNone {
            context.getThemeColor(R.attr.colorControlActivated)
        }
        private val tabProgressTrackColorNormal: Int by lazyNone {
            context.getThemeColor(R.attr.colorOnSurface).withAlpha(Alpha.p10)
        }
        private val tabProgressTrackColorSelected: Int by lazyNone {
            context.getThemeColor(R.attr.colorControlActivated).withAlpha(Alpha.p20)
        }

        // endregion
    }

    data class ViewModel(
        val jlptLessons: List<JlptLesson>,
        val hankoDisplay: HankoDisplaySetting
    )
}
