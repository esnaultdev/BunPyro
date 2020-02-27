package dev.esnault.bunpyro.android.res

import dev.esnault.bunpyro.R
import dev.esnault.bunpyro.domain.entities.JLPT


val JLPT.textResId: Int
    get() = when (this) {
        JLPT.N5 -> R.string.jlpt_n5
        JLPT.N4 -> R.string.jlpt_n4
        JLPT.N3 -> R.string.jlpt_n3
        JLPT.N2 -> R.string.jlpt_n2
        JLPT.N1 -> R.string.jlpt_n1
    }
