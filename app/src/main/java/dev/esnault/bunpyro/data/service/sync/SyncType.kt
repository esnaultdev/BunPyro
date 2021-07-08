package dev.esnault.bunpyro.data.service.sync


enum class SyncType(val value: String) {
    /** Sync of all grammar data and reviews. */
    ALL("all"),
    /** Sync of the reviews. */
    REVIEWS("reviews");

    companion object {
        fun fromValue(value: String?): SyncType {
            return when (value) {
                REVIEWS.value -> REVIEWS
                else -> ALL
            }
        }
    }
}
