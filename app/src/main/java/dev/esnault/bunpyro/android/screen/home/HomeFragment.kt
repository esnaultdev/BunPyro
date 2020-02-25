package dev.esnault.bunpyro.android.screen.home


import dev.esnault.bunpyro.android.screen.base.BaseFragment
import dev.esnault.bunpyro.databinding.FragmentHomeBinding
import org.koin.android.viewmodel.ext.android.viewModel


class HomeFragment : BaseFragment<FragmentHomeBinding>() {

    override val vm: HomeViewModel by viewModel()
    override val viewBindingClass = FragmentHomeBinding::class
}
