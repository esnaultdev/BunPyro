package dev.esnault.bunpyro.android.screen.settings.about


import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import dev.esnault.bunpyro.android.screen.ScreenConfig
import dev.esnault.bunpyro.android.screen.base.BaseFragment
import dev.esnault.bunpyro.android.screen.base.BaseViewModel
import dev.esnault.bunpyro.common.openUrlInBrowser
import dev.esnault.bunpyro.databinding.FragmentSettingsAboutBinding

class SettingsAboutFragment : BaseFragment<FragmentSettingsAboutBinding>() {

    override val vm: BaseViewModel? = null
    override val bindingClass = FragmentSettingsAboutBinding::class

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        NavigationUI.setupWithNavController(binding.toolbar, findNavController())

        binding.bunproButton.setOnClickListener {
            context?.openUrlInBrowser(ScreenConfig.Url.bunpro)
        }

        binding.devButton.setOnClickListener {
            context?.openUrlInBrowser(ScreenConfig.Url.devWebsite)
        }

        binding.sourcesButton.setOnClickListener {
            context?.openUrlInBrowser(ScreenConfig.Url.githubRepo)
        }
    }
}