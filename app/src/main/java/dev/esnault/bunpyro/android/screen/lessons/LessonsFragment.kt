package dev.esnault.bunpyro.android.screen.lessons

import dev.esnault.bunpyro.android.screen.base.BaseFragment
import dev.esnault.bunpyro.databinding.FragmentLessonsBinding
import org.koin.android.viewmodel.ext.android.viewModel


class LessonsFragment : BaseFragment<FragmentLessonsBinding>() {

    override val vm: LessonsViewModel by viewModel()
    override val bindingClass = FragmentLessonsBinding::class
}
