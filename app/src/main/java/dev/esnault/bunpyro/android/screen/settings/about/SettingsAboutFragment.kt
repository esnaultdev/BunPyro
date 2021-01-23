package dev.esnault.bunpyro.android.screen.settings.about


import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import dev.esnault.bunpyro.android.screen.ScreenConfig
import dev.esnault.bunpyro.android.screen.base.BaseFragment
import dev.esnault.bunpyro.android.screen.base.BaseViewModel
import dev.esnault.bunpyro.android.utils.setupWithNav
import dev.esnault.bunpyro.common.openUrlInBrowser
import dev.esnault.bunpyro.data.analytics.Analytics
import dev.esnault.bunpyro.databinding.FragmentSettingsAboutBinding

class SettingsAboutFragment : BaseFragment<FragmentSettingsAboutBinding>() {

    override val vm: BaseViewModel? = null
    override val bindingClass = FragmentSettingsAboutBinding::class

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Analytics.screen(name = "settings.about")

        binding.toolbar.setupWithNav(findNavController())

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
