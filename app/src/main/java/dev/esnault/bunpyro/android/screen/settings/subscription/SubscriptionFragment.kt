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
import java.text.SimpleDateFormat
import java.util.*

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
            context?.openUrlInBrowser(ScreenConfig.Url.bunproSubscribe)
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
private fun ExpiredPreview() {
    Preview(
        viewState = ViewState(
            subscription = UserSubscription(
                status = SubscriptionStatus.EXPIRED,
                lastCheck = null
            ),
            refreshing = false
        )
    )
}

@Preview
@Composable
private fun RefreshingPreview() {
    Preview(
        viewState = ViewState(
            subscription = UserSubscription(
                status = SubscriptionStatus.SUBSCRIBED,
                lastCheck = null
            ),
            refreshing = true
        )
    )
}

@Preview
@Composable
private fun SubscribedPreview() {
    Preview(
        viewState = ViewState(
            subscription = UserSubscription(
                status = SubscriptionStatus.SUBSCRIBED,
                lastCheck = Date()
            ),
            refreshing = false
        )
    )
}

@Preview
@Composable
private fun NotSubscribedPreview() {
    Preview(
        viewState = ViewState(
            subscription = UserSubscription(
                status = SubscriptionStatus.NOT_SUBSCRIBED,
                lastCheck = Date()
            ),
            refreshing = false
        )
    )
}

@Composable
private fun Preview(viewState: ViewState) {
    AboutContent(
        navController = null,
        viewState = viewState,
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
        title = stringResource(R.string.settings_root_user_subscription)
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
        val subStatus = viewState.subscription.status
        val typography = MaterialTheme.typography
        val maxTextWidth = dimensionResource(R.dimen.text_max_width)
        val textModifier = Modifier.widthIn(max = maxTextWidth)

        val normalMargin = 8.dp
        val smallMargin = 4.dp

        Text(
            text = stringResource(R.string.subscription_status_title),
            style = typography.caption,
            modifier = textModifier
        )

        Spacer(modifier = Modifier.height(smallMargin))

        val statusTextResId = when (subStatus) {
            SubscriptionStatus.SUBSCRIBED -> R.string.subscription_status_subscribed
            SubscriptionStatus.NOT_SUBSCRIBED -> R.string.subscription_status_notSubscribed
            SubscriptionStatus.EXPIRED -> R.string.subscription_status_expired
        }
        Text(
            text = stringResource(statusTextResId),
            style = typography.body1,
            modifier = textModifier
        )
        Spacer(modifier = Modifier.height(normalMargin))

        val lastCheck = viewState.subscription.lastCheck
        if (lastCheck != null) {
            Text(
                text = stringResource(R.string.subscription_lastCheck_title),
                style = typography.caption,
                modifier = textModifier
            )
            Spacer(modifier = Modifier.height(smallMargin))

            Text(
                text = SimpleDateFormat.getDateTimeInstance().format(lastCheck),
                style = typography.body2,
                modifier = textModifier
            )
            Spacer(modifier = Modifier.height(normalMargin))
        }

        TextButton(onClick = listener.onRefreshClick) {
            if (viewState.refreshing) {
                CircularProgressIndicator(modifier = Modifier.size(16.dp))
            } else {
                Text(stringResource(R.string.subscription_refresh))
            }
        }
        Spacer(modifier = Modifier.height(24.dp))

        val descriptionResId = when (subStatus) {
            SubscriptionStatus.SUBSCRIBED -> R.string.subscription_explanation_subscribed
            SubscriptionStatus.NOT_SUBSCRIBED -> R.string.subscription_explanation_notSubscribed
            SubscriptionStatus.EXPIRED -> R.string.subscription_explanation_expired
        }
        Text(text = stringResource(descriptionResId))

        Spacer(modifier = Modifier.height(normalMargin))
        if (subStatus == SubscriptionStatus.NOT_SUBSCRIBED) {
            Button(onClick = listener.onSubscribeClick) {
                Text(text = stringResource(R.string.subscription_subscribe))
            }
        } else if (subStatus == SubscriptionStatus.SUBSCRIBED) {
            TextButton(onClick = listener.onDetailsClick) {
                Text(text = stringResource(R.string.subscription_seeMore))
            }
        }
    }
}
