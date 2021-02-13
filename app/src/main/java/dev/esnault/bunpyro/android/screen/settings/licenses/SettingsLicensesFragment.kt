package dev.esnault.bunpyro.android.screen.settings.licenses


import android.os.Bundle
import android.view.View
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumnForIndexed
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Providers
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import dev.esnault.bunpyro.R
import dev.esnault.bunpyro.android.display.compose.AppTheme
import dev.esnault.bunpyro.android.display.compose.SimpleScreen
import dev.esnault.bunpyro.android.screen.base.BaseViewModel
import dev.esnault.bunpyro.android.screen.base.ComposeFragment
import dev.esnault.bunpyro.common.openUrlInBrowser
import dev.esnault.bunpyro.data.analytics.Analytics

class SettingsLicensesFragment : ComposeFragment() {

    override val vm: BaseViewModel? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Analytics.screen(name = "settings.licenses")
    }

    @Composable
    override fun FragmentContent() {
        LicensesContent(
            navController = findNavController(),
            openUrl = { url -> context?.openUrlInBrowser(url) },
            licenses = licenses
        )
    }
}

@Preview
@Composable
private fun DefaultPreview() {
    LicensesContent(
        navController = null,
        openUrl = {},
        licenses = licenses
    )
}

@Composable
private fun LicensesContent(
    navController: NavController?,
    openUrl: (url: String) -> Unit,
    licenses: List<License>
) {
    SimpleScreen(
        navController = navController,
        title = stringResource(R.string.settings_root_licenses)
    ) {
        BodyContent(openUrl, licenses)
    }
}

@Composable
private fun BodyContent(
    openUrl: (url: String) -> Unit,
    licenses: List<License>
) {
    val lastIndex: Int = licenses.lastIndex
    LazyColumnForIndexed(items = licenses) { index, license ->
        LicenseItem(openUrl = openUrl, license = license)
        if (index != lastIndex) {
            Divider(modifier = Modifier.fillParentMaxWidth())
        }
    }
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFFFFFF
)
@Composable
private fun LicenseItemPreview() {
    AppTheme {
        LicenseItem(openUrl = { }, license = licenses.first())
    }
}

@Composable
private fun LicenseItem(
    openUrl: (url: String) -> Unit,
    license: License
) {
    val typography = MaterialTheme.typography
    val rowModifier = Modifier
        .fillMaxWidth()
        .clickable(onClick = { openUrl(license.url) })
        .padding(horizontal = 16.dp, vertical = 8.dp)
    Column(modifier = rowModifier) {
        Text(text = license.title, style = typography.body1)
        Spacer(modifier = Modifier.preferredHeight(4.dp))
        Providers(AmbientContentAlpha provides ContentAlpha.medium) {
            Text(text = license.summary, style = typography.body2)
            Spacer(modifier = Modifier.preferredHeight(4.dp))
            Text(text = license.license, style = typography.body2)
        }
    }
}

