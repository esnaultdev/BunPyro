package dev.esnault.bunpyro.android.screen.grammarpoint

import androidx.navigation.fragment.navArgs
import dev.esnault.bunpyro.android.screen.base.BaseFragment
import dev.esnault.bunpyro.databinding.FragmentGrammarPointBinding
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf


class GrammarPointFragment : BaseFragment<FragmentGrammarPointBinding>() {

    private val args: GrammarPointFragmentArgs by navArgs()
    override val vm: GrammarPointViewModel by viewModel(parameters = { parametersOf(args) })
    override val bindingClass = FragmentGrammarPointBinding::class
}
