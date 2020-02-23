package dev.esnault.bunpyro.android.screen.apikey

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import dev.esnault.bunpyro.R
import dev.esnault.bunpyro.android.screen.base.BaseFragment
import org.koin.android.viewmodel.ext.android.viewModel


class ApiKeyFragment : BaseFragment() {

    override val vm: ApiKeyViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_api_key, container, false)
    }
}
