package dev.esnault.bunpyro.android.screen.base

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.NavDirections


open class BaseViewModel : ViewModel() {

    private val _navigationCommands = SingleLiveEvent<NavigationCommand>()
    val navigationCommands: LiveData<NavigationCommand>
        get() = _navigationCommands

    fun navigate(command: NavigationCommand) {
        _navigationCommands.postValue(command)
    }

    fun navigate(directions: NavDirections) {
        _navigationCommands.postValue(NavigationCommand.To(directions))
    }
}
