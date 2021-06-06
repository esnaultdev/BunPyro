package dev.esnault.bunpyro.android.screen.settings.subscription


import android.os.Bundle
import android.view.View
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
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
import dev.esnault.bunpyro.android.screen.ScreenConfig
import dev.esnault.bunpyro.android.screen.base.ComposeFragment
import dev.esnault.bunpyro.android.screen.settings.subscription.SubscriptionViewModel.ViewState
import dev.esnault.bunpyro.common.openUrlInBrowser
import dev.esnault.bunpyro.data.analytics.Analytics
import dev.esnault.bunpyro.domain.entities.user.SubscriptionStatus
import dev.esnault.bunpyro.domain.entities.user.UserSubscription
import org.koin.androidx.viewmodel.ext.android.viewModel

class SubscriptionFragment : ComposeFragment() {

    override val vm: SubscriptionViewModel by viewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Analytics.screen(name = "settings.subscription")
    }

    override fun onResume() {
        super.onResume()
        vm.onResume()
    }

    @Composable
    override fun FragmentContent() {
        val openBunproSubscription: () -> Unit = {
            context?.openUrlInBrowser(ScreenConfig.Url.devWebsite)
        }
        val viewState: ViewState? by vm.viewState.observeAsState(null)
        AboutContent(
            navController = findNavController(),
            viewState = viewState,
            listener = ContentListener(
                onRefreshClick = { vm.onRefresh() },
                onSubscribeClick = openBunproSubscription,
                onDetailsClick = openBunproSubscription
            )
        )
    }
}

private class ContentListener(
    val onRefreshClick: () -> Unit = {},
    val onSubscribeClick: () -> Unit = {},
    val onDetailsClick: () -> Unit = {}
)

@Preview
@Composable
private fun DefaultPreview() {
    AboutContent(
        navController = null,
        viewState = ViewState(
            subscription = UserSubscription.DEFAULT,
            refreshing = false
        ),
        listener = ContentListener()
    )
}

@Composable
private fun AboutContent(
    navController: NavController?,
    viewState: ViewState?,
    listener: ContentListener
) {
    SimpleScreen(
        navController = navController,
        title = stringResource(R.string.settings_root_about)
    ) {
        if (viewState != null) {
            BodyContent(viewState, listener)
        }
    }
}

@Composable
private fun BodyContent(viewState: ViewState, listener: ContentListener) {
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

        val statusTextResId = when (viewState.subscription.status) {
            SubscriptionStatus.SUBSCRIBED -> R.string.subscription_status_subscribed
            SubscriptionStatus.NOT_SUBSCRIBED -> R.string.subscription_status_notSubscribed
            SubscriptionStatus.EXPIRED -> R.string.subscription_status_expired
        }
        Text(
            text = stringResource(statusTextResId),
            style = typography.body1,
            modifier = textModifier
        )

        Spacer(modifier = Modifier.height(buttonMargin))
        TextButton(onClick = listener.onRefreshClick) {
            Text(stringResource(R.string.subscription_refresh))
        }
        Spacer(modifier = Modifier.height(buttonMargin))
    }
}
