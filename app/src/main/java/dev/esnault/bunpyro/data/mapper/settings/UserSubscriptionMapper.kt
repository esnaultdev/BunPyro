package dev.esnault.bunpyro.data.mapper.settings

import androidx.annotation.Keep
import dev.esnault.bunpyro.data.mapper.IMapper
import dev.esnault.bunpyro.data.mapper.manualMappingMoshi
import dev.esnault.bunpyro.domain.entities.user.UserSubscription
import dev.esnault.bunpyro.domain.entities.user.SubscriptionStatus as SubStatus
import java.util.*


private val moshi = manualMappingMoshi

class UserSubscriptionFromStringMapper : IMapper<String?, UserSubscription> {

    override fun map(o: String?): UserSubscription {
        if (o == null) return UserSubscription.DEFAULT

        val jsoned = moshi.adapter(UserSubscriptionJson::class.java).fromJson(o)
        if (jsoned == null) return UserSubscription.DEFAULT

        return UserSubscription(
            status = if (jsoned.subscribed) SubStatus.SUBSCRIBED else SubStatus.NOT_SUBSCRIBED,
            lastCheck = jsoned.lastCheck?.let(::Date)
        )
    }
}

class UserSubscriptionToStringMapper : IMapper<UserSubscription, String> {

    override fun map(o: UserSubscription): String {
        val subscribed: Boolean = when (o.status) {
            SubStatus.NOT_SUBSCRIBED -> false
            SubStatus.SUBSCRIBED, SubStatus.EXPIRED -> true
        }
        val jsonable = UserSubscriptionJson(
            subscribed = subscribed,
            lastCheck = o.lastCheck?.time
        )
        return moshi.adapter(UserSubscriptionJson::class.java).toJson(jsonable)
    }
}

/**
 * Alternate version of [UserSubscription] made for easier JSON parsing.
 */
@Keep
private data class UserSubscriptionJson(
    val subscribed: Boolean,
    val lastCheck: Long?
)
