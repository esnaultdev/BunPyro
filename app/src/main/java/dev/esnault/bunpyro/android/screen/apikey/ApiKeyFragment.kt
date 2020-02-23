package dev.esnault.bunpyro.android.screen.apikey

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.observe

import dev.esnault.bunpyro.android.screen.base.BaseFragment
import dev.esnault.bunpyro.databinding.FragmentApiKeyBinding
import org.koin.android.viewmodel.ext.android.viewModel


class ApiKeyFragment : BaseFragment() {

    private var _binding: FragmentApiKeyBinding? = null
    private val binding get() = _binding!!

    override val vm: ApiKeyViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentApiKeyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apikeyInputField.setOnEditorActionListener { _, actionId, _ ->
            onInputFieldEditorAction(actionId)
        }

        binding.apikeyInputField.doAfterTextChanged { text ->
            vm.apiKeyUpdated(text?.toString())
        }

        binding.apikeySave.setOnClickListener {
            vm.saveApiKey()
        }

        binding.apikeyPrivacy.setOnClickListener {
            // TODO Open the browser to https://www.bunpro.jp/privacy
        }

        vm.canSend.observe(this) { canSend ->
            binding.apikeySave.isEnabled = canSend
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    private fun onInputFieldEditorAction(actionId: Int): Boolean {
        return if (actionId == EditorInfo.IME_ACTION_DONE) {
            vm.saveApiKey()
            true
        } else {
            false
        }
    }
}
