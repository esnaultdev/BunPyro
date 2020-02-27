package dev.esnault.bunpyro.android.screen.lessons

import android.os.Bundle
import android.view.View
import dev.esnault.bunpyro.android.res.textResId
import dev.esnault.bunpyro.android.screen.base.BaseFragment
import dev.esnault.bunpyro.databinding.FragmentLessonsBinding
import dev.esnault.bunpyro.databinding.TabJlptBinding
import dev.esnault.bunpyro.domain.entities.JLPT
import org.koin.android.viewmodel.ext.android.viewModel


class LessonsFragment : BaseFragment<FragmentLessonsBinding>() {

    override val vm: LessonsViewModel by viewModel()
    override val bindingClass = FragmentLessonsBinding::class

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupTabs()
    }

    private fun setupTabs() {
        for (i in 5 downTo 1) {
            val tabs = binding.tabs
            tabs.newTab().apply {
                val tabBinding = TabJlptBinding.inflate(layoutInflater)
                customView = tabBinding.root
                tag = tabBinding

                setText(JLPT[i].textResId)

                // TODO properly bind these tests values
                tabBinding.progress.max = 128
                tabBinding.progress.progress = 64

                tabs.addTab(this)
            }
        }
    }
}
