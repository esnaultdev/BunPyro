package dev.esnault.bunpyro.android.display.span

import android.text.style.ClickableSpan
import android.view.View


/**
 * A span displaying a link to a grammar point
 */
class GrammarLinkSpan(
    val grammarPointId: Long,
    var listener: (id: Long) -> Unit
) : ClickableSpan() {

    override fun onClick(widget: View) {
        listener(grammarPointId)
    }
}
