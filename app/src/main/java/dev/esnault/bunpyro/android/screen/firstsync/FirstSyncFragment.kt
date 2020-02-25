package dev.esnault.bunpyro.android.screen.firstsync


import dev.esnault.bunpyro.android.screen.base.BaseFragment
import dev.esnault.bunpyro.databinding.FragmentFirstSyncBinding
import org.koin.android.viewmodel.ext.android.viewModel

class FirstSyncFragment : BaseFragment<FragmentFirstSyncBinding>() {

    override val vm: FirstSyncViewModel by viewModel()
    override val viewBindingClass = FragmentFirstSyncBinding::class
}
