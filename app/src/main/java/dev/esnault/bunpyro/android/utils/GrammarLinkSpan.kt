package dev.esnault.bunpyro.android.utils

import android.text.style.ClickableSpan
import android.view.View


/**
 * A span displaying a link to a grammar point
 */
class GrammarLinkSpan(
    val grammarPointId: Int,
    var listener: (id: Int) -> Unit
) : ClickableSpan() {

    override fun onClick(widget: View) {
        listener(grammarPointId)
    }
}
