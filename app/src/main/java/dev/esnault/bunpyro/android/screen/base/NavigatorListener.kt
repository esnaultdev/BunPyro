package dev.esnault.bunpyro.android.screen.base

import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController


class NavigatorListener(fragment: Fragment, navigationCommands: LiveData<NavigationCommand>) {

    private val navController = fragment.findNavController()
    private val finish = fun() {
        fragment.activity?.finish()
    }

    init {
        navigationCommands.observe(fragment) { command ->
            when (command) {
                is NavigationCommand.To ->
                    navController.navigate(command.directions)
                is NavigationCommand.Back -> {
                    if (!navController.popBackStack()) {
                        // If we're at the root of the back stack, we want the activity to close
                        finish()
                    }
                }
                is NavigationCommand.BackTo ->
                    navController.popBackStack(command.destinationId, false)
            }
        }
    }
}
