package dev.esnault.bunpyro.domain.entities.settings


enum class HankoDisplaySetting(val value: String) {
    NORMAL("normal"),
    LEVEL("level");

    companion object {
        val DEFAULT = NORMAL

        fun fromValue(value: String?): HankoDisplaySetting {
            return when (value) {
                NORMAL.value -> NORMAL
                else -> LEVEL
            }
        }
    }
}
