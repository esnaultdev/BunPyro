package dev.esnault.bunpyro.android.screen.firstsync


import android.os.Bundle
import android.view.View
import androidx.lifecycle.observe
import androidx.transition.AutoTransition
import androidx.transition.TransitionManager
import dev.esnault.bunpyro.R
import dev.esnault.bunpyro.android.screen.base.BaseFragment
import dev.esnault.bunpyro.common.hide
import dev.esnault.bunpyro.common.show
import dev.esnault.bunpyro.databinding.FragmentFirstSyncBinding
import dev.esnault.bunpyro.android.screen.firstsync.FirstSyncViewModel.ViewState as ViewState
import org.koin.android.viewmodel.ext.android.viewModel

class FirstSyncFragment : BaseFragment<FragmentFirstSyncBinding>() {

    override val vm: FirstSyncViewModel by viewModel()
    override val bindingClass = FragmentFirstSyncBinding::class

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        vm.viewState.observe(this) { viewState -> bindViewState(viewState) }

        binding.errorButton.setOnClickListener {
            vm.onRetry()
        }
    }

    private fun bindViewState(viewState: ViewState) {
        TransitionManager.beginDelayedTransition(binding.rootConstraintLayout, AutoTransition())

        when (viewState) {
            is ViewState.Downloading -> {
                binding.downloadingGroup.show()
                binding.errorGroup.hide()
            }
            is ViewState.Error -> {
                binding.errorGroup.show()
                binding.downloadingGroup.hide()

                binding.errorText.text = getString(viewState.titleResId)
                binding.errorSubtext.text = getString(viewState.textResId)
            }
        }
    }

    private val ViewState.Error.titleResId: Int
        get() = when (this) {
            is ViewState.Error.Unknown -> R.string.firstsync_error_unknown_title
            is ViewState.Error.Network -> R.string.firstsync_error_network_title
        }

    private val ViewState.Error.textResId: Int
        get() = when (this) {
            is ViewState.Error.Unknown -> R.string.firstsync_error_unknown_text
            is ViewState.Error.Network -> R.string.firstsync_error_network_text
        }
}
