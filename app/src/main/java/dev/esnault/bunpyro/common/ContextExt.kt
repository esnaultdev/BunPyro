package dev.esnault.bunpyro.common


import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.content.res.TypedArray
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.util.TypedValue
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat


fun Context.openUrlInBrowser(url: String) {
    val intent = Intent(Intent.ACTION_VIEW)
        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        .setData(Uri.parse(url))
    startActivity(intent)
}

fun Context.openNotificationSettingsCompat(channelId: String) {
    if (Build.VERSION.SDK_INT >= 26) {
        openNotificationSettings(channelId)
    }
}

@RequiresApi(26)
private fun Context.openNotificationSettings(channelId: String) {
    val settingsIntent: Intent = Intent(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS)
        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        .putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
        .putExtra(Settings.EXTRA_CHANNEL_ID, channelId)
    startActivity(settingsIntent)
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
    val imm = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(view.windowToken, 0)
}

fun Context.isDarkTheme(): Boolean {
    return resources.configuration.uiMode and
            Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
}
