package dev.esnault.bunpyro.android.screen.lessons

import android.os.Bundle
import android.view.View
import androidx.lifecycle.observe
import dev.esnault.bunpyro.android.res.textResId
import dev.esnault.bunpyro.android.screen.base.BaseFragment
import dev.esnault.bunpyro.databinding.FragmentLessonsBinding
import dev.esnault.bunpyro.databinding.TabJlptBinding
import dev.esnault.bunpyro.domain.entities.JLPT
import dev.esnault.bunpyro.android.screen.lessons.LessonsViewModel.ViewState as ViewState
import org.koin.android.viewmodel.ext.android.viewModel


class LessonsFragment : BaseFragment<FragmentLessonsBinding>() {

    override val vm: LessonsViewModel by viewModel()
    override val bindingClass = FragmentLessonsBinding::class
    private val tabsBinding = mutableMapOf<JLPT, TabJlptBinding>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupTabs()

        vm.viewState.observe(this) { viewState -> bindViewState(viewState) }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        tabsBinding.clear()
    }

    private fun setupTabs() {
        for (i in 5 downTo 1) {
            val tabs = binding.tabs
            tabs.newTab().apply {
                val jlpt = JLPT[i]

                val tabBinding = TabJlptBinding.inflate(layoutInflater)
                customView = tabBinding.root

                setText(jlpt.textResId)

                tabs.addTab(this)
                tabsBinding[jlpt] = tabBinding
            }
        }
    }

    private fun bindViewState(viewState: ViewState) {
        bindTabs(viewState)
    }

    private fun bindTabs(viewState: ViewState) {
        viewState.lessons.forEach { jlptLesson ->
            tabsBinding[jlptLesson.level]?.let { tabBinding ->
                tabBinding.progress.max = jlptLesson.size
                tabBinding.progress.progress = jlptLesson.studied
            }
        }
    }
}
