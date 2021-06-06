package dev.esnault.bunpyro.domain.entities.settings


enum class MockSubscriptionSetting(val value: String) {
    ACTUAL("actual"),
    SUBSCRIBED("subscribed"),
    NOT_SUBSCRIBED("not_subscribed"),
    EXPIRED("expired");

    companion object {
        fun fromValue(value: String?): MockSubscriptionSetting =
            values().firstOrNull { it.value == value } ?: ACTUAL
    }
}
