package dev.esnault.bunpyro.common


import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.TypedArray
import android.net.Uri
import android.util.TypedValue
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat


fun Context.openUrlInBrowser(url: String) {
    val i = Intent(Intent.ACTION_VIEW)
    i.data = Uri.parse(url)
    startActivity(i)
}

@ColorInt
fun Context.getColorCompat(@ColorRes colorResId: Int): Int {
    return ContextCompat.getColor(this, colorResId)
}

@ColorInt
fun Context.getThemeColor(@AttrRes colorAttrId: Int): Int {
    val typedValue = TypedValue()

    val a: TypedArray = obtainStyledAttributes(typedValue.data, intArrayOf(colorAttrId))
    val color = a.getColor(0, 0)

    a.recycle()
    return color
}

fun Context.hideKeyboardFrom(view: View) {
    val imm: InputMethodManager =
        getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(view.windowToken, 0)
}
