package dev.esnault.bunpyro.utils

import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.test.espresso.IdlingResource
import androidx.test.espresso.IdlingResource.ResourceCallback


class NavigationIdlingResource(
    private val navController: NavController,
    private val destinationId: Int
) : IdlingResource {

    private var callback: ResourceCallback? = null
    private var reached = false

    override fun getName(): String {
        return "NavigationIdlingResource:$destinationId"
    }

    override fun isIdleNow(): Boolean {
        return reached
    }

    override fun registerIdleTransitionCallback(callback: ResourceCallback) {
        this.callback = callback
    }

    init {
        navController.addOnDestinationChangedListener(object : NavController.OnDestinationChangedListener {
            override fun onDestinationChanged(
                controller: NavController,
                destination: NavDestination,
                arguments: Bundle?
            ) {
                if (!reached && destination.id == destinationId) {
                    reached = true
                    callback?.onTransitionToIdle()
                    navController.removeOnDestinationChangedListener(this)
                }
            }
        })
    }
}
