package dev.esnault.bunpyro.domain.entities.settings


enum class NightModeSetting {
    ALWAYS,
    NEVER,
    SYSTEM;

    companion object {
        fun fromValue(value: String?): NightModeSetting {
            return when (value) {
                "always" -> ALWAYS
                "never" -> NEVER
                else -> SYSTEM
            }
        }
    }
}
