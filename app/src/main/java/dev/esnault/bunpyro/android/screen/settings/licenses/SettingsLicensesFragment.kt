package dev.esnault.bunpyro.android.screen.settings.licenses


import android.os.Bundle
import android.view.View
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
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
            licenses = licenses,
            onLicenseClick = { license ->
                context?.openUrlInBrowser(license.url)
            }
        )
    }
}

@Preview
@Composable
private fun DefaultPreview() {
    LicensesContent(
        navController = null,
        licenses = licenses,
        onLicenseClick = {}
    )
}

@Composable
private fun LicensesContent(
    navController: NavController?,
    licenses: List<License>,
    onLicenseClick: (license: License) -> Unit
) {
    SimpleScreen(
        navController = navController,
        title = stringResource(R.string.settings_root_licenses)
    ) {
        BodyContent(licenses, onLicenseClick)
    }
}

@Composable
private fun BodyContent(
    licenses: List<License>,
    onLicenseClick: (license: License) -> Unit
) {
    val lastIndex: Int = licenses.lastIndex
    LazyColumn {
        itemsIndexed(items = licenses) {  index, license ->
            LicenseItem(license = license, onClick = { onLicenseClick(license) })
            if (index != lastIndex) {
                Divider(modifier = Modifier.fillParentMaxWidth())
            }
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
        LicenseItem(license = licenses.first(), onClick = {})
    }
}

@Composable
private fun LicenseItem(
    license: License,
    onClick: () -> Unit
) {
    val typography = MaterialTheme.typography
    val rowModifier = Modifier
        .fillMaxWidth()
        .clickable(onClick = onClick)
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
