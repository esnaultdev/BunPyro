package dev.esnault.bunpyro.common

import android.util.DisplayMetrics
import kotlin.math.roundToInt


fun Float.spToPx(displayMetrics: DisplayMetrics): Int =
    (this * displayMetrics.scaledDensity).roundToInt()

fun Float.spToPxRaw(displayMetrics: DisplayMetrics): Float = this * displayMetrics.scaledDensity

fun Float.dpToPx(displayMetrics: DisplayMetrics): Int =
    (this * displayMetrics.density).roundToInt()

fun Float.dpToPxRaw(displayMetrics: DisplayMetrics): Float = this * displayMetrics.density
