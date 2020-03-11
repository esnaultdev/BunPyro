package dev.esnault.bunpyro.android.screen.home


import android.os.Bundle
import android.view.View
import dev.esnault.bunpyro.android.screen.base.BaseFragment
import dev.esnault.bunpyro.databinding.FragmentHomeBinding
import org.koin.android.viewmodel.ext.android.viewModel


class HomeFragment : BaseFragment<FragmentHomeBinding>() {

    override val vm: HomeViewModel by viewModel()
    override val bindingClass = FragmentHomeBinding::class

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.lessonsButton.setOnClickListener {
            vm.onLessonsTap()
        }

        binding.allGrammarButton.setOnClickListener {
            vm.onAllGrammarTap()
        }
    }
}
