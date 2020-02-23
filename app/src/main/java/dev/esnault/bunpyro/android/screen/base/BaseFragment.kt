package dev.esnault.bunpyro.android.screen.base

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController


abstract class BaseFragment : Fragment() {

    abstract val vm: BaseViewModel?

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        vm?.navigationCommands?.observe(this) { command ->
            val navController = findNavController()
            when (command) {
                is NavigationCommand.To ->
                    navController.navigate(command.directions)
                is NavigationCommand.Back ->
                    navController.popBackStack()
                is NavigationCommand.BackTo ->
                    navController.popBackStack(command.destinationId, false)
            }
        }
    }
}
