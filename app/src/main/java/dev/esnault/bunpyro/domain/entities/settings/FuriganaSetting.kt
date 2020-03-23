package dev.esnault.bunpyro.domain.entities.settings


enum class FuriganaSetting {
    SHOWN,
    HIDDEN;

    companion object {
        fun fromValue(value: String?): FuriganaSetting {
            return when (value) {
                "shown" -> SHOWN
                else -> HIDDEN
            }
        }
    }
}
