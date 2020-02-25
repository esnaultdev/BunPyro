package dev.esnault.bunpyro.android.screen.apikey

import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.observe
import androidx.transition.AutoTransition
import androidx.transition.TransitionManager
import dev.esnault.bunpyro.R

import dev.esnault.bunpyro.android.screen.base.BaseFragment
import dev.esnault.bunpyro.common.hide
import dev.esnault.bunpyro.common.hideKeyboard
import dev.esnault.bunpyro.common.openUrlInBrowser
import dev.esnault.bunpyro.common.show
import dev.esnault.bunpyro.databinding.FragmentApiKeyBinding
import org.koin.android.viewmodel.ext.android.viewModel


class ApiKeyFragment : BaseFragment<FragmentApiKeyBinding>() {

    override val vm: ApiKeyViewModel by viewModel()
    override val viewBindingClass = FragmentApiKeyBinding::class

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apikeyInputField.setOnEditorActionListener { _, actionId, _ ->
            onInputFieldEditorAction(actionId)
        }

        binding.apikeyInputField.doAfterTextChanged { text ->
            vm.apiKeyUpdated(text?.toString())
        }

        binding.apikeySave.setOnClickListener {
            vm.onSaveApiKey()
        }

        binding.apikeyPrivacy.setOnClickListener {
            context?.openUrlInBrowser("https://www.bunpro.jp/privacy")
        }

        binding.apikeyErrorButton.setOnClickListener {
            vm.onErrorOk()
        }

        vm.viewState.observe(this) { viewState ->
            bindToViewState(viewState)
        }
    }

    private fun onInputFieldEditorAction(actionId: Int): Boolean {
        return if (actionId == EditorInfo.IME_ACTION_DONE) {
            vm.onSaveApiKey()
            true
        } else {
            false
        }
    }

    private fun bindToViewState(viewState: ApiKeyViewModel.ViewState) {
        TransitionManager.beginDelayedTransition(binding.apikeyRoot, AutoTransition())

        when (viewState) {
            is ApiKeyViewModel.ViewState.Default -> {
                binding.apikeyDefaultGroup.show()
                binding.apikeyCheckingGroup.hide()
                binding.apikeyErrorGroup.hide()
                binding.apikeyWelcome.hide()

                binding.apikeySave.isEnabled = viewState.canSend
            }
            ApiKeyViewModel.ViewState.Checking -> {
                binding.apikeyCheckingGroup.show()
                binding.apikeyDefaultGroup.hide()
                binding.apikeyErrorGroup.hide()
                binding.apikeyWelcome.hide()
            }
            is ApiKeyViewModel.ViewState.Success -> {
                binding.apikeyWelcome.show()
                binding.apikeyDefaultGroup.hide()
                binding.apikeyCheckingGroup.hide()
                binding.apikeyErrorGroup.hide()

                binding.apikeyWelcome.text = getString(R.string.apikey_welcome, viewState.name)
            }
            is ApiKeyViewModel.ViewState.Error -> {
                binding.apikeyErrorGroup.show()
                binding.apikeyDefaultGroup.hide()
                binding.apikeyCheckingGroup.hide()
                binding.apikeyWelcome.hide()

                binding.apikeyErrorText.text = getString(viewState.titleResId)
                binding.apikeyErrorSubtext.text = getString(viewState.textResId)
            }
        }

        if (viewState !is ApiKeyViewModel.ViewState.Default) {
            view?.rootView?.hideKeyboard()
        }
    }

    private val ApiKeyViewModel.ViewState.Error.titleResId: Int
        get() = when (this) {
            ApiKeyViewModel.ViewState.Error.Network -> R.string.apikey_check_error_network_title
            ApiKeyViewModel.ViewState.Error.Invalid -> R.string.apikey_check_error_invalid_title
            ApiKeyViewModel.ViewState.Error.Server,
            ApiKeyViewModel.ViewState.Error.Unknown -> R.string.apikey_check_error_unknown_title
        }

    private val ApiKeyViewModel.ViewState.Error.textResId: Int
        get() = when (this) {
            ApiKeyViewModel.ViewState.Error.Network -> R.string.apikey_check_error_network_text
            ApiKeyViewModel.ViewState.Error.Invalid -> R.string.apikey_check_error_invalid_text
            ApiKeyViewModel.ViewState.Error.Server,
            ApiKeyViewModel.ViewState.Error.Unknown -> R.string.apikey_check_error_unknown_text
        }
}
