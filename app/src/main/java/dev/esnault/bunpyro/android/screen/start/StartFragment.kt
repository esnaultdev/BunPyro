package dev.esnault.bunpyro.android.screen.start


import dev.esnault.bunpyro.android.screen.base.BaseFragment
import dev.esnault.bunpyro.databinding.FragmentStartBinding
import org.koin.android.viewmodel.ext.android.viewModel


/**
 * A start fragment used for the initial navigation.
 */
class StartFragment : BaseFragment<FragmentStartBinding>() {

    override val vm: StartViewModel by viewModel()
    override val bindingClass = FragmentStartBinding::class
}
