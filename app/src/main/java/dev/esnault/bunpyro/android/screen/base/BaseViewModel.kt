package dev.esnault.bunpyro.android.screen.base

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.NavDirections
import dev.esnault.bunpyro.di.utils.getKoinInstance


open class BaseViewModel : ViewModel() {

    protected val navigator: Navigator = getKoinInstance()

    val navigationCommands: LiveData<NavigationCommand>
        get() = navigator.navigationCommands

    protected fun navigate(command: NavigationCommand) {
        navigator.navigate(command)
    }

    protected fun navigate(directions: NavDirections) {
        navigator.navigate(NavigationCommand.To(directions))
    }
}
