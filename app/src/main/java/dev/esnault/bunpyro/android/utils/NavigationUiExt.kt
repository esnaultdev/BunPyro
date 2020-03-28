package dev.esnault.bunpyro.android.utils

import androidx.appcompat.graphics.drawable.DrawerArrowDrawable
import androidx.appcompat.widget.Toolbar
import androidx.navigation.NavController
import androidx.navigation.ui.NavigationUI
import dev.esnault.bunpyro.R
import dev.esnault.bunpyro.common.getThemeColor


/**
 * Setup a toolbar with a [NavController] and set the navigation icon color to
 * [R.attr.secondaryColorOnToolbar].
 */
fun Toolbar.setupWithNav(navController: NavController) {
    NavigationUI.setupWithNavController(this, navController)

    // On API 21 the toolbar icon color is red in dark mode so we need to set is manually
    // See https://github.com/material-components/material-components-android/issues/1119
    val toolbarIconColor = context.getThemeColor(R.attr.secondaryColorOnToolbar)
    navigationIcon?.let { it as? DrawerArrowDrawable }
        ?.let { it.color = toolbarIconColor }
}
