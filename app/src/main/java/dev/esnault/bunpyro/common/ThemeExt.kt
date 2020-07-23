package dev.esnault.bunpyro.common

import android.content.res.Resources.Theme
import android.util.TypedValue
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt


@ColorInt
fun Theme.getThemeColor(@AttrRes colorAttrId: Int): Int {
    val typedValue = TypedValue()

    resolveAttribute(colorAttrId, typedValue, true)
    return typedValue.data
}
