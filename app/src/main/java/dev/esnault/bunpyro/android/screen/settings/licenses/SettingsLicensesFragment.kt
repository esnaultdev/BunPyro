package dev.esnault.bunpyro.android.screen.settings.licenses


import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import dev.esnault.bunpyro.android.screen.base.BaseFragment
import dev.esnault.bunpyro.android.screen.base.BaseViewModel
import dev.esnault.bunpyro.android.utils.setupWithNav
import dev.esnault.bunpyro.databinding.FragmentSettingsLicensesBinding

class SettingsLicensesFragment : BaseFragment<FragmentSettingsLicensesBinding>() {

    override val vm: BaseViewModel? = null
    override val bindingClass = FragmentSettingsLicensesBinding::class

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = LicensesAdapter(context)
        }

        binding.toolbar.setupWithNav(findNavController())
    }
}
