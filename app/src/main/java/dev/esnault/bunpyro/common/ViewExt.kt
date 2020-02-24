package dev.esnault.bunpyro.common

import android.app.Activity
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
