package dev.esnault.bunpyro.data.mapper.settings

import androidx.annotation.Keep
import dev.esnault.bunpyro.data.mapper.IMapper
import dev.esnault.bunpyro.data.mapper.manualMappingMoshi
import dev.esnault.bunpyro.domain.entities.user.UserSubscription
import java.util.*


private val moshi = manualMappingMoshi

class UserSubscriptionFromStringMapper : IMapper<String?, UserSubscription> {

    override fun map(o: String?): UserSubscription {
        if (o == null) return UserSubscription.DEFAULT

        val jsoned = moshi.adapter(UserSubscriptionJson::class.java).fromJson(o)
        if (jsoned == null) return UserSubscription.DEFAULT

        return UserSubscription(
            subscribed = jsoned.subscribed,
            lastCheck = jsoned.lastCheck?.let(::Date)
        )
    }
}

class UserSubscriptionToStringMapper : IMapper<UserSubscription, String> {

    override fun map(o: UserSubscription): String {
        val jsonable = UserSubscriptionJson(
            subscribed = o.subscribed,
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
