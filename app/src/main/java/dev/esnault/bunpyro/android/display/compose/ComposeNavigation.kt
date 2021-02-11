package dev.esnault.bunpyro.android.display.compose

import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.navigation.NavController


@Composable
fun NavigateBackIcon(navController: NavController?) {
    IconButton(
        onClick = { navController?.popBackStack() }
    ) {
        Icon(imageVector = Icons.Filled.ArrowBack)
    }
}
