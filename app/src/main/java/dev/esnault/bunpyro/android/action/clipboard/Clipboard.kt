package dev.esnault.bunpyro.android.action.clipboard

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context


class Clipboard(context: Context) : IClipboard {

    private val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

    override fun copy(label: String, text: String) {
        val clip = ClipData.newPlainText(label, text)
        clipboard.primaryClip = clip
    }
}
