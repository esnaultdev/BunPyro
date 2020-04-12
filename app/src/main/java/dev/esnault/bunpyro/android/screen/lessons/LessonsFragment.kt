package dev.esnault.bunpyro.android.screen.lessons

import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.graphics.drawable.ScaleDrawable
import android.os.Bundle
import android.view.View
import androidx.lifecycle.observe
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import dev.esnault.bunpyro.R
import dev.esnault.bunpyro.android.res.textResId
import dev.esnault.bunpyro.android.screen.base.BaseFragment
import dev.esnault.bunpyro.android.screen.lessons.LessonsViewModel.SnackBarMessage
import dev.esnault.bunpyro.android.screen.lessons.LessonsViewModel.ViewState
import dev.esnault.bunpyro.common.Alpha
import dev.esnault.bunpyro.common.getThemeColor
import dev.esnault.bunpyro.common.withAlpha
import dev.esnault.bunpyro.databinding.FragmentLessonsBinding
import dev.esnault.bunpyro.databinding.TabJlptLessonBinding
import dev.esnault.bunpyro.domain.entities.JLPT
import org.koin.androidx.viewmodel.ext.android.viewModel


class LessonsFragment : BaseFragment<FragmentLessonsBinding>() {

    override val vm: LessonsViewModel by viewModel()
    override val bindingClass = FragmentLessonsBinding::class

    private var lessonsAdapter: JlptLessonAdapter? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupPager()
        setupTabs()
        bindPagerToTabs()

        vm.viewState.observe(this) { viewState -> bindViewState(viewState) }
        vm.snackbar.observe(this) { message -> showSnackbar(message) }
    }

    private fun setupPager() {
        if (lessonsAdapter == null) {
            lessonsAdapter = JlptLessonAdapter(context!!) { vm.onGrammarClicked(it) }
        }
        binding.jlptLessonsPager.adapter = lessonsAdapter
        binding.jlptLessonsPager.isUserInputEnabled = false
    }

    private fun bindPagerToTabs() {
        TabLayoutMediator(binding.jlptLessonsTabs, binding.jlptLessonsPager) { tab, position ->
            tab.apply {
                val jlpt = JLPT[5 - position]

                val tabBinding = TabJlptLessonBinding.inflate(layoutInflater)
                customView = tabBinding.root
                tag = tabBinding

                tabBinding.title.text = getString(jlpt.textResId)
                updateTabColors(tabBinding, false)

                lessonsAdapter?.jlptLessons?.get(position)?.let { jlptLesson ->
                    tabBinding.apply {
                        progress.max = jlptLesson.size
                        progress.progress = jlptLesson.studied
                    }
                }
            }
        }.attach()
    }

    private fun bindViewState(viewState: ViewState) {
        val newViewModel = JlptLessonAdapter.ViewModel(viewState.lessons, viewState.hankoDisplay)
        lessonsAdapter?.viewModel = newViewModel
    }

    private fun showSnackbar(message: SnackBarMessage) {
        val textResId = when (message) {
            is SnackBarMessage.Incomplete -> R.string.common_grammarPoint_incomplete
        }

        // We're using the coordinator layout as the context view to have the swipe to dismiss
        // gesture
        val contextView = binding.coordinatorLayout
        Snackbar.make(contextView, textResId, Snackbar.LENGTH_SHORT)
            .show()
    }

    // region Tabs

    private fun setupTabs() {
        binding.jlptLessonsTabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {}

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                (tab?.tag as? TabJlptLessonBinding)?.let { updateTabColors(it, false) }
            }

            override fun onTabSelected(tab: TabLayout.Tab?) {
                (tab?.tag as? TabJlptLessonBinding)?.let { updateTabColors(it, true) }
            }
        })
    }

    // Update the progress bar colors for the selected tab.
    // Using a colorStateList for the progress and background drawables doesn't work on API 21
    // so we need to update the drawables colors manually
    private fun updateTabColors(tabBinding: TabJlptLessonBinding, selected: Boolean) {
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
        requireContext().getThemeColor(R.attr.colorOnPrimary).withAlpha(Alpha.p40)
    }
    private val tabProgressColorSelected: Int by lazy(LazyThreadSafetyMode.NONE) {
        requireContext().getThemeColor(R.attr.colorOnPrimary)
    }
    private val tabBackgroundColorNormal: Int by lazy(LazyThreadSafetyMode.NONE) {
        requireContext().getThemeColor(R.attr.colorOnPrimary).withAlpha(Alpha.p20)
    }
    private val tabBackgroundColorSelected: Int by lazy(LazyThreadSafetyMode.NONE) {
        requireContext().getThemeColor(R.attr.colorOnPrimary).withAlpha(Alpha.p30)
    }

    // endregion
}
