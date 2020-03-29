package dev.esnault.bunpyro.domain.entities


enum class JLPT(val level: Int) {
    N5(5), N4(4), N3(3), N2(2), N1(1);

    companion object {
        operator fun get(level: Int): JLPT {
            return when (level) {
                5 -> N5
                4 -> N4
                3 -> N3
                2 -> N2
                1 -> N1
                else -> throw IllegalArgumentException("Invalid JLPT level: N$level")
            }
        }
    }
}
