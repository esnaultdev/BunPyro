package dev.esnault.bunpyro.android.screen.settings.subscription

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dev.esnault.bunpyro.android.screen.base.BaseViewModel
import dev.esnault.bunpyro.data.service.user.IUserService
import dev.esnault.bunpyro.domain.entities.user.UserSubscription
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch


class SubscriptionViewModel(
    private val userService: IUserService
) : BaseViewModel() {

    private val _viewState = MutableLiveData<ViewState>()
    val viewState: LiveData<ViewState>
        get() = _viewState

    init {
        viewModelScope.launch(Dispatchers.IO) {
            userService.subscription.collect { subscription ->
                // TODO Collect the refresh status
                val newState = ViewState(subscription, false)
                _viewState.postValue(newState)
            }
        }
    }

    // region Events

    fun onRefresh() {
        userService.refreshSubscription(force = true)
    }

    fun onResume() {
        userService.refreshSubscription()
    }

    // endregion

    data class ViewState(
        val subscription: UserSubscription,
        val refreshing: Boolean
    )
}
