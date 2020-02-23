package dev.esnault.bunpyro.android.screen.start


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import dev.esnault.bunpyro.R
import dev.esnault.bunpyro.android.screen.base.BaseFragment
import org.koin.android.viewmodel.ext.android.viewModel


/**
 * A start fragment used for the initial navigation.
 */
class StartFragment : BaseFragment() {

    override val vm: StartViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_start, container, false)
    }
}
