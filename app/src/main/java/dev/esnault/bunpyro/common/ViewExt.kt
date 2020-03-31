package dev.esnault.bunpyro.common

import android.app.Activity
import android.graphics.Rect
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager


fun View.show() {
    visibility = View.VISIBLE
}

fun View.invisible() {
    visibility = View.VISIBLE
}

fun View.hide() {
    visibility = View.GONE
}

fun View.hideKeyboard() {
    val imm = context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(windowToken, 0)
}

/**
 * Remove the focus from the current focused view if the user touches outside of it.
 *
 * Note that this uses the [View.OnTouchListener] of the view.
 * If this listener is needed for anything else, use another approach.
 */
fun View.addFocusRemover(activity: Activity) {
    setOnTouchListener { _, event ->
        if (event.action == MotionEvent.ACTION_DOWN) {
            val focusedView = activity.window.currentFocus
            if (focusedView != null) {
                val outRect = Rect()
                focusedView.getGlobalVisibleRect(outRect)
                if (!outRect.contains(event.rawX.toInt(), event.rawY.toInt())) {
                    focusedView.clearFocus()
                }
            }
        }
        false
    }
}
