package dev.esnault.bunpyro.android.screen.allgrammar


import dev.esnault.bunpyro.android.screen.base.BaseFragment
import dev.esnault.bunpyro.databinding.FragmentAllGrammarBinding
import org.koin.android.viewmodel.ext.android.viewModel


class AllGrammarFragment : BaseFragment<FragmentAllGrammarBinding>() {

    override val vm: AllGrammarViewModel by viewModel()
    override val bindingClass = FragmentAllGrammarBinding::class

}
