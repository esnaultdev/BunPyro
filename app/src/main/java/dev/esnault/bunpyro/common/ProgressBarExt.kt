package dev.esnault.bunpyro.common

import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.graphics.drawable.ScaleDrawable
import android.view.Gravity


/**
 * Build a horizontal progress drawable programmatically.
 */
fun buildHorizontalProgressDrawable(
    backgroundColor: Int,
    progressColor: Int,
    cornerRadius: Float = 0f
): Drawable {
    return LayerDrawable(
        arrayOf(
            GradientDrawable().apply {
                setColor(backgroundColor)
                this.cornerRadius = cornerRadius
            },
            ScaleDrawable(
                GradientDrawable().apply {
                    setColor(progressColor)
                    this.cornerRadius = cornerRadius
                },
                Gravity.START,
                1f,
                -1f
            )
        )
    ).apply {
        setId(0, android.R.id.background)
        setId(1, android.R.id.progress)
    }
}
