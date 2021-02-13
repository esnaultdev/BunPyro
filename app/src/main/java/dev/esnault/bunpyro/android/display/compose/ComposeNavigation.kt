package dev.esnault.bunpyro.android.display.compose

import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import dev.esnault.bunpyro.R


@Composable
fun NavigateBackIcon(navController: NavController?) {
    IconButton(
        onClick = { navController?.popBackStack() }
    ) {
        Icon(
            imageVector = Icons.Filled.ArrowBack,
            contentDescription = stringResource(R.string.contentDescription_navigateBack)
        )
    }
}
