package dev.esnault.bunpyro.android.screen.lessons

import android.os.Bundle
import android.view.View
import androidx.lifecycle.observe
import com.google.android.material.tabs.TabLayoutMediator
import dev.esnault.bunpyro.android.res.textResId
import dev.esnault.bunpyro.android.screen.base.BaseFragment
import dev.esnault.bunpyro.android.screen.lessons.LessonsViewModel.ViewState
import dev.esnault.bunpyro.databinding.FragmentLessonsBinding
import dev.esnault.bunpyro.databinding.TabLessonBinding
import dev.esnault.bunpyro.domain.entities.JLPT
import org.koin.android.viewmodel.ext.android.viewModel


class LessonsFragment : BaseFragment<FragmentLessonsBinding>() {

    override val vm: LessonsViewModel by viewModel()
    override val bindingClass = FragmentLessonsBinding::class

    private var lessonsAdapter: JlptLessonAdapter? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupPager()
        bindPagerToTabs()

        vm.viewState.observe(this) { viewState -> bindViewState(viewState) }
    }

    private fun setupPager() {
        lessonsAdapter = JlptLessonAdapter(context!!)
        binding.pager.adapter = lessonsAdapter
        binding.pager.isUserInputEnabled = false
    }

    private fun bindPagerToTabs() {
        TabLayoutMediator(binding.tabs, binding.pager) { tab, position ->
            tab.apply {
                val jlpt = JLPT[5 - position]

                val tabBinding = TabLessonBinding.inflate(layoutInflater)
                customView = tabBinding.root

                setText(jlpt.textResId)

                lessonsAdapter?.jlptLessons?.get(position)?.let { jlptLesson ->
                    tabBinding.progress.max = jlptLesson.size
                    tabBinding.progress.progress = jlptLesson.studied
                }
            }
        }.attach()
    }

    private fun bindViewState(viewState: ViewState) {
        lessonsAdapter?.jlptLessons = viewState.lessons
    }
}
