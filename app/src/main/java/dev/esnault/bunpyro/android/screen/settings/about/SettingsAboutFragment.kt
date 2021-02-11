package dev.esnault.bunpyro.android.screen.settings.about


import android.os.Bundle
import android.view.View
import androidx.compose.foundation.ScrollableColumn
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import dev.esnault.bunpyro.R
import dev.esnault.bunpyro.android.display.compose.AppTheme
import dev.esnault.bunpyro.android.display.compose.NavigateBackIcon
import dev.esnault.bunpyro.android.screen.ScreenConfig
import dev.esnault.bunpyro.android.screen.base.BaseViewModel
import dev.esnault.bunpyro.android.screen.base.ComposeFragment
import dev.esnault.bunpyro.common.openUrlInBrowser
import dev.esnault.bunpyro.data.analytics.Analytics

class SettingsAboutFragment : ComposeFragment() {

    override val vm: BaseViewModel? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Analytics.screen(name = "settings.about")
    }

    @Preview
    @Composable
    override fun FragmentContent() {
        AboutContent(
            navController = findNavController(),
            openUrl = { url -> context?.openUrlInBrowser(url) }
        )
    }
}

@Preview
@Composable
private fun DefaultPreview() {
    AboutContent(navController = null, openUrl = {})
}

@Composable
private fun AboutContent(
    navController: NavController?,
    openUrl: (url: String) -> Unit
) {
    AppTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(stringResource(R.string.settings_root_about)) },
                    navigationIcon = { NavigateBackIcon(navController = navController) }
                )
            },
            bodyContent = {
                BodyContent(openUrl)
            }
        )
    }
}

@Composable
private fun BodyContent(openUrl: (url: String) -> Unit) {
    ScrollableColumn(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val typography = MaterialTheme.typography
        val maxTextWidth = dimensionResource(R.dimen.text_max_width)
        val textModifier = Modifier.preferredWidthIn(max = maxTextWidth)

        val buttonMargin = 8.dp

        Text(
            text = stringResource(R.string.about_intro),
            style = typography.body1,
            modifier = textModifier
        )

        Spacer(modifier = Modifier.preferredHeight(buttonMargin))
        TextButton(
            onClick = { openUrl(ScreenConfig.Url.bunpro) }
        ) {
            Text(stringResource(R.string.about_checkBunpro))
        }
        Spacer(modifier = Modifier.preferredHeight(buttonMargin))

        Text(
            text = stringResource(R.string.about_affiliation),
            style = typography.body2,
            modifier = textModifier
        )
        SpacedDivider()
        Text(
            text = stringResource(R.string.about_dev_text),
            style = typography.body1,
            modifier = textModifier
        )

        Spacer(modifier = Modifier.preferredHeight(buttonMargin))
        TextButton(
            onClick = { openUrl(ScreenConfig.Url.devWebsite) }
        ) {
            Text(stringResource(R.string.about_dev_website))
        }
        Spacer(modifier = Modifier.preferredHeight(buttonMargin))

        Text(
            text = stringResource(R.string.about_api),
            style = typography.body2,
            modifier = textModifier
        )
        SpacedDivider()
        Text(
            text = stringResource(R.string.about_sources_text),
            style = typography.body1,
            modifier = textModifier
        )

        Spacer(modifier = Modifier.preferredHeight(buttonMargin))
        TextButton(
            onClick = { openUrl(ScreenConfig.Url.githubRepo) }
        ) {
            Text(stringResource(R.string.about_sources_action))
        }
    }
}

@Composable
fun SpacedDivider() {
    val dividerWidth = 144.dp
    val dividerMargin = 16.dp

    Spacer(modifier = Modifier.preferredHeight(dividerMargin))
    Divider(modifier = Modifier.preferredWidth(dividerWidth))
    Spacer(modifier = Modifier.preferredHeight(dividerMargin))
}
