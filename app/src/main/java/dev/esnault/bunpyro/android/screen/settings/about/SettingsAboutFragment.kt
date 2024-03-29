package dev.esnault.bunpyro.android.screen.settings.about


import android.os.Bundle
import android.view.View
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import dev.esnault.bunpyro.android.display.compose.SimpleScreen
import dev.esnault.bunpyro.android.screen.base.ComposeFragment
import dev.esnault.bunpyro.data.analytics.Analytics
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsAboutFragment : ComposeFragment() {

    override val vm: SettingsAboutViewModel by viewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Analytics.screen(name = "settings.about")
    }

    @Composable
    override fun FragmentContent() {
        AboutContent(
            navController = findNavController(),
            listener = ContentListener(
                onBunproClick = vm::onBunproClick,
                onDevWebsiteClick = vm::onDevWebsiteClick,
                onGithubRepoClick = vm::onGithubRepoClick
            )
        )
    }
}

private class ContentListener(
    val onBunproClick: () -> Unit = {},
    val onDevWebsiteClick: () -> Unit = {},
    val onGithubRepoClick: () -> Unit = {}
)

@Preview
@Composable
private fun DefaultPreview() {
    AboutContent(navController = null, listener = ContentListener())
}

@Composable
private fun AboutContent(
    navController: NavController?,
    listener: ContentListener
) {
    SimpleScreen(
        navController = navController,
        title = stringResource(R.string.settings_root_about)
    ) {
        BodyContent(listener)
    }
}

@Composable
private fun BodyContent(listener: ContentListener) {
    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val typography = MaterialTheme.typography
        val maxTextWidth = dimensionResource(R.dimen.text_max_width)
        val textModifier = Modifier.widthIn(max = maxTextWidth)

        val buttonMargin = 8.dp

        Text(
            text = stringResource(R.string.about_intro),
            style = typography.body1,
            modifier = textModifier
        )

        Spacer(modifier = Modifier.height(buttonMargin))
        TextButton(onClick = listener.onBunproClick) {
            Text(stringResource(R.string.about_checkBunpro))
        }
        Spacer(modifier = Modifier.height(buttonMargin))

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

        Spacer(modifier = Modifier.height(buttonMargin))
        TextButton(onClick = listener.onDevWebsiteClick) {
            Text(stringResource(R.string.about_dev_website))
        }
        Spacer(modifier = Modifier.height(buttonMargin))

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

        Spacer(modifier = Modifier.height(buttonMargin))
        TextButton(onClick = listener.onGithubRepoClick) {
            Text(stringResource(R.string.about_sources_action))
        }
    }
}

@Composable
private fun SpacedDivider() {
    val dividerWidth = 144.dp
    val dividerMargin = 16.dp

    Spacer(modifier = Modifier.height(dividerMargin))
    Divider(modifier = Modifier.width(dividerWidth))
    Spacer(modifier = Modifier.height(dividerMargin))
}
