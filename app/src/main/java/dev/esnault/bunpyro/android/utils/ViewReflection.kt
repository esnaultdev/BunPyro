package dev.esnault.bunpyro.android.utils

import android.view.View
import com.google.android.material.textfield.TextInputLayout


/**
 * The [View] of the start icon of a [TextInputLayout].
 * Since it's private, try to get it using reflection.
 */
val TextInputLayout.startIconView: View?
    get() {
        return try {
            val field = TextInputLayout::class.java.getDeclaredField("startIconView")
            field.isAccessible = true
            field.get(this) as? View
        } catch (e: Exception) {
            null
        }
    }

/**
 * The [View] of the end icon of a [TextInputLayout].
 * Since it's private, try to get it using reflection.
 */
val TextInputLayout.endIconView: View?
    get() {
        return try {
            val field = TextInputLayout::class.java.getDeclaredField("endIconView")
            field.isAccessible = true
            field.get(this) as? View
        } catch (e: Exception) {
            null
        }
    }
