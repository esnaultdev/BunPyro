package dev.esnault.bunpyro.android.screen.base

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.NavDirections


open class BaseViewModel : ViewModel() {

    private val navigator = Navigator()

    val navigationCommands: LiveData<NavigationCommand>
        get() = navigator.navigationCommands

    fun navigate(command: NavigationCommand) {
        navigator.navigate(command)
    }

    fun navigate(directions: NavDirections) {
        navigator.navigate(NavigationCommand.To(directions))
    }
}
