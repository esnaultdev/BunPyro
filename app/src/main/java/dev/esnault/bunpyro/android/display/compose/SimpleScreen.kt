package dev.esnault.bunpyro.android.display.compose

import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.navigation.NavController

/**
 * A simple screen with a top app bar and some [content] below.
 */
@Composable
fun SimpleScreen(
    navController: NavController?,
    title: String,
    content: @Composable () -> Unit
) {
    AppTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(title) },
                    navigationIcon = { NavigateBackIcon(navController = navController) }
                )
            },
            content = {
                content()
            }
        )
    }
}
