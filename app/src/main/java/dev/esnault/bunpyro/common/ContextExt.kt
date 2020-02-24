package dev.esnault.bunpyro.common

import android.content.Context
import android.content.Intent
import android.net.Uri


fun Context.openUrlInBrowser(url: String) {
    val i = Intent(Intent.ACTION_VIEW)
    i.data = Uri.parse(url)
    startActivity(i)
}
