package dev.esnault.bunpyro.android.screen.base

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.navigation.NavDirections
import dev.esnault.bunpyro.common.openUrlInBrowser


class Navigator(private val appContext: Context) {
    private val _navigationCommands = SingleLiveEvent<NavigationCommand>()
    val navigationCommands: LiveData<NavigationCommand>
        get() = _navigationCommands

    fun navigate(command: NavigationCommand) {
        _navigationCommands.postValue(command)
    }

    fun navigate(directions: NavDirections) {
        _navigationCommands.postValue(NavigationCommand.To(directions))
    }

    fun openUrlInBrowser(url: String) {
        appContext.openUrlInBrowser(url)
    }
}
