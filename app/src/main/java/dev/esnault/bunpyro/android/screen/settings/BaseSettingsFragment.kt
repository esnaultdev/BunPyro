package dev.esnault.bunpyro.android.screen.settings

import android.os.Bundle
import android.view.View
import androidx.annotation.StringRes
import androidx.annotation.XmlRes
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceFragmentCompat
import dev.esnault.bunpyro.R
import dev.esnault.bunpyro.android.screen.base.BaseFragment
import dev.esnault.bunpyro.android.screen.base.BaseViewModel
import dev.esnault.bunpyro.android.utils.setupWithNav
import dev.esnault.bunpyro.common.getIntOrNull
import dev.esnault.bunpyro.databinding.FragmentSettingsBinding


abstract class BaseSettingsFragment<VM : BaseViewModel> : BaseFragment<FragmentSettingsBinding>() {

    abstract override val vm: VM?
    abstract val toolbarTitleResId: Int
        @StringRes get
    abstract val settingResId: Int
        @XmlRes get

    override val bindingClass = FragmentSettingsBinding::class

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.apply {
            setupWithNav(findNavController())
            title = requireContext().getString(toolbarTitleResId)
        }

        val prefFragment = SettingsPreferenceFragment.create(
            settingResourceId = settingResId,
            setup = ::setup
        )
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, prefFragment)
            .commit()
    }

    private fun setup(fragment: SettingsPreferenceFragment) = fragment.setupPreferences()

    abstract fun SettingsPreferenceFragment.setupPreferences()
}

/**
 * Since we can't add a toolbar to the preference fragment directly,
 * it's wrapped into another fragment.
 */
class SettingsPreferenceFragment : PreferenceFragmentCompat() {

    private var settingResId: Int? = null
    private var setup: (SettingsPreferenceFragment) -> Unit = {}

    override fun onCreate(savedInstanceState: Bundle?) {
        settingResId = arguments?.getIntOrNull(EXTRA_SETTING_RESOURCE_ID)
        super.onCreate(savedInstanceState)
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        settingResId?.let { settingResourceId ->
            setPreferencesFromResource(settingResourceId, rootKey)
            setup(this)
        }
    }

    companion object {
        private const val EXTRA_SETTING_RESOURCE_ID: String = "extra:settingResId"

        fun create(
            settingResourceId: Int,
            setup: (SettingsPreferenceFragment) -> Unit
        ): SettingsPreferenceFragment {
            return SettingsPreferenceFragment().apply {
                arguments = Bundle().apply {
                    putInt(EXTRA_SETTING_RESOURCE_ID, settingResourceId)
                }
                this@apply.setup = setup
            }
        }
    }
}
