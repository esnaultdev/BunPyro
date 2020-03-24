package dev.esnault.bunpyro.android.screen.lessons

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.lifecycle.observe
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayoutMediator
import dev.esnault.bunpyro.R
import dev.esnault.bunpyro.android.res.textResId
import dev.esnault.bunpyro.android.screen.base.BaseFragment
import dev.esnault.bunpyro.android.screen.lessons.LessonsViewModel.SnackBarMessage
import dev.esnault.bunpyro.android.screen.lessons.LessonsViewModel.ViewState
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

                tabBinding.title.text = getString(jlpt.textResId)

                lessonsAdapter?.jlptLessons?.get(position)?.let { jlptLesson ->
                    tabBinding.apply {
                        if (jlptLesson.completed) {
                            progress.visibility = View.INVISIBLE
                            completedHanko.isVisible = true
                        } else {
                            progress.visibility = View.VISIBLE
                            completedHanko.isVisible = false

                            progress.max = jlptLesson.size
                            progress.progress = jlptLesson.studied
                        }
                    }
                }
            }
        }.attach()
    }

    private fun bindViewState(viewState: ViewState) {
        lessonsAdapter?.jlptLessons = viewState.lessons
    }

    private fun showSnackbar(message: SnackBarMessage) {
        val textResId = when (message) {
            is SnackBarMessage.Incomplete -> R.string.lessons_grammar_point_incomplete
        }

        // We're using the coordinator layout as the context view to have the swipe to dismiss
        // gesture
        val contextView = binding.coordinatorLayout
        Snackbar.make(contextView, textResId, Snackbar.LENGTH_SHORT)
            .show()
    }
}
